package photon.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import photon.gallery.Panel;
import photon.model.Catalog;
import photon.model.Dynamo;
import photon.model.User;
import photon.util.EQueue;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class MixedGalleryService implements GalleryService {

    private MfwCrudService mfwCrud;
    private static EQueue<Catalog> displayQueue;
    private static EQueue<Catalog> cacheQueue;
    private final ReentrantLock lock = new ReentrantLock();

    private static int DEFAULT_BUFFER_SIZE = 64;
    private static int DEFAULT_VIEW_THRESHOLD = 12;

    @Autowired
    public MixedGalleryService(MfwCrudService mfwCrud) {
        this.mfwCrud = mfwCrud;
    }

    @Override
    public boolean init() {
        lock.lock();
        try {
            if (displayQueue == null) {
                displayQueue = (cacheQueue == null) ? preload() : cacheQueue;
                new Thread(() -> cacheQueue = preload()).start();
            }
            return displayQueue != null;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Panel[] nextBatch(int batchSize) {
        Set<Integer> articleIdSet = new HashSet<>();
        while (articleIdSet.size() < batchSize) {
            if (displayQueue.isEmpty() && !init()) {
                break;
            }
            Integer i = displayQueue.dequeue().getArticleId();
            articleIdSet.add(i);
        }
        if (!articleIdSet.isEmpty()) {
            new Thread(() -> mfwCrud.batchIncrementViewCount(articleIdSet, User.DEFAULT_USER_ID)).start();
            return batchGetFromDynamo(articleIdSet.toArray(new Integer[batchSize]));
        }
        return new Panel[0];
    }

    private EQueue<Catalog> preload() {
        List<Catalog> c = mfwCrud.randomBuffer(DEFAULT_BUFFER_SIZE, DEFAULT_VIEW_THRESHOLD);
        if (c != null) {
            EQueue<Catalog> queue = new EQueue<>();
            c.forEach(queue::enqueue);
            return queue;
        }
        return null;
    }

    private Panel[] batchGetFromDynamo(Integer[] articleIds) {
        return Dynamo.helper().batchGetAsJson(articleIds).stream().map(Panel::new).toArray(Panel[]::new);
    }
}
