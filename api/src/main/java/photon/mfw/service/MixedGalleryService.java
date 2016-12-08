package photon.mfw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import photon.mfw.model.Panel;
import photon.mfw.model.Catalog;
import photon.util.PQueue;

import java.util.*;

@Service
public class MixedGalleryService implements GalleryService {

    private CrudService mfwCrud;
    private final Map<Integer, PQueue<Catalog>> displayQueues;
    private Map<Integer, PQueue<Catalog>> cacheQueues;

    private static final int DEFAULT_BUFFER_SIZE = 128;
    private static final int DEFAULT_VIEW_THRESHOLD = 32;

    @Autowired
    public MixedGalleryService(CrudService mfwCrud) {
        this.mfwCrud = mfwCrud;
        displayQueues = new HashMap<>();
        cacheQueues = new HashMap<>();
    }

    @Override
    public void init(Integer userId) {
        _init(userId);
    }

    private PQueue<Catalog> _init(Integer userId) {
        synchronized (displayQueues) {
            PQueue<Catalog> dq = displayQueues.get(userId);
            if (dq != null && !dq.isEmpty()) return dq;
            PQueue<Catalog> cache = cacheQueues.get(userId);
            if (cache == null) cache = preload(userId);
            displayQueues.put(userId, cache);
            new Thread(() -> cacheQueues.put(userId, preload(userId))).start();
            return cache;
        }
    }

    @Override
    public Panel[] nextBatch(Integer userId, int batchSize) {
        Set<Integer> articleIdSet = new HashSet<>();
        PQueue<Catalog> dq = displayQueues.get(userId);
        if (dq == null) dq = _init(userId);
        while (articleIdSet.size() < batchSize) {
            if (dq.isEmpty()) dq = _init(userId);
            Integer i = dq.dequeue().getArticleId();
            articleIdSet.add(i);
        }
        if (!articleIdSet.isEmpty()) {
            new Thread(() -> mfwCrud.batchIncrementViewCount(articleIdSet, userId)).start();
            return batchGetFromDynamo(articleIdSet.toArray(new Integer[batchSize]));
        }
        return new Panel[0];
    }

    private PQueue<Catalog> preload(Integer userId) {
        List<Catalog> c = mfwCrud.randomBuffer(userId, DEFAULT_BUFFER_SIZE, DEFAULT_VIEW_THRESHOLD);
        if (c == null) throw new RuntimeException("Failed to preload!");
        PQueue<Catalog> queue = new PQueue<>();
        c.forEach(queue::enqueue);
        return queue;
    }

    private Panel[] batchGetFromDynamo(Integer[] articleIds) {
        return DynamoService.helper().batchGetAsJson(articleIds).stream().map(Panel::new).toArray(Panel[]::new);
    }
}
