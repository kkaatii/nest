package photon.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import photon.gallery.Panel;
import photon.model.Catalog;
import photon.model.Dynamo;
import photon.util.EQueue;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class MixedGalleryService implements GalleryService {

    private MfwCrudService mfwCrud;
    private static Map<Integer, EQueue<Catalog>> displayQueues;
    private static Map<Integer, EQueue<Catalog>> cacheQueues;
    private final ReentrantLock lock = new ReentrantLock();

    private static int DEFAULT_BUFFER_SIZE = 64;
    private static int DEFAULT_VIEW_THRESHOLD = 12;

    @Autowired
    public MixedGalleryService(MfwCrudService mfwCrud) {
        this.mfwCrud = mfwCrud;
        displayQueues = new HashMap<>();
        cacheQueues = new HashMap<>();
    }

    @Override
    public void init(Integer userId) {
        EQueue<Catalog> cache = cacheQueues.get(userId);
        displayQueues.put(userId, cache == null ? preload(userId) : cache);
        new Thread(() -> cacheQueues.put(userId, preload(userId))).start();
    }

    @Override
    public Panel[] nextBatch(Integer userId, int batchSize) {
        Set<Integer> articleIdSet = new HashSet<>();
        EQueue<Catalog> display = displayQueues.get(userId);
        while (articleIdSet.size() < batchSize) {
            if (display.isEmpty()) {
                init(userId);
                display = displayQueues.get(userId);
            }
            Integer i = display.dequeue().getArticleId();
            articleIdSet.add(i);
        }
        if (!articleIdSet.isEmpty()) {
            new Thread(() -> mfwCrud.batchIncrementViewCount(articleIdSet, userId)).start();
            return batchGetFromDynamo(articleIdSet.toArray(new Integer[batchSize]));
        }
        return new Panel[0];
    }

    private EQueue<Catalog> preload(Integer userId) {
        List<Catalog> c = mfwCrud.randomBuffer(userId, DEFAULT_BUFFER_SIZE, DEFAULT_VIEW_THRESHOLD);
        if (c == null) throw new RuntimeException("Failed to preload!");
        EQueue<Catalog> queue = new EQueue<>();
        c.forEach(queue::enqueue);
        return queue;
    }

    private Panel[] batchGetFromDynamo(Integer[] articleIds) {
        return Dynamo.helper().batchGetAsJson(articleIds).stream().map(Panel::new).toArray(Panel[]::new);
    }
}
