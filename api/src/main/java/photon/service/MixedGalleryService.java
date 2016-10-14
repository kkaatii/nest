package photon.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import photon.gallery.Panel;
import photon.model.Catalog;
import photon.model.Dynamo;
import photon.model.User;
import photon.util.EQueue;

import java.util.*;

@Service
public class MixedGalleryService implements GalleryService {

    private MfwCrudService mfwCrud;
    private static EQueue<Catalog> displayQueue;
    private static EQueue<Catalog> cacheQueue;

    private static int DEFAULT_BUFFER_SIZE = 64;
    private static int DEFAULT_VIEW_THRESHOLD = 12;

    @Autowired
    public MixedGalleryService(MfwCrudService mfwCrud) {
        this.mfwCrud = mfwCrud;
        //cacheQueue = preload(DEFAULT_BUFFER_SIZE);
    }

    @Override
    public boolean init() {
        displayQueue = (cacheQueue == null) ? preload(DEFAULT_BUFFER_SIZE) : cacheQueue;
        new Thread(() -> cacheQueue = preload(DEFAULT_BUFFER_SIZE)).start();
        return displayQueue != null;
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
        new Thread(() -> mfwCrud.batchIncrementViewCount(articleIdSet, User.DEFAULT_USER_ID)).start();
        return batchGetFromDynamo(articleIdSet.toArray(new Integer[batchSize]));
    }

    private EQueue<Catalog> preload(int bufferSize) {
        List<Catalog> c = mfwCrud.randomBuffer(bufferSize, DEFAULT_VIEW_THRESHOLD);
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
