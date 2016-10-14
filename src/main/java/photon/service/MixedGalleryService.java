package photon.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import photon.gallery.Panel;
import photon.model.Catalog;
import photon.model.Dynamo;

import java.security.SecureRandom;
import java.util.*;

@Service
public class MixedGalleryService implements GalleryService {

    private CrudService crudService;

    private static List<Catalog> catalogs;
    private static Set<Integer> displayed;
    private static SecureRandom rnd;

    @Autowired
    public MixedGalleryService(CrudService crudService) {
        this.crudService = crudService;
        displayed = new HashSet<>();
        rnd = new SecureRandom();
    }

    @Override
    public boolean init() {
        if (preload()) {
            displayed.clear();
            return true;
        }
        return false;
    }

    @Override
    public Panel[] nextBatch(int batchSize) {
        int size = catalogs.size();
        Integer[] cids = new Integer[batchSize];
        int pos;
        for (int i = 0; i < batchSize; i++) {
            if (displayed.size() == size) {
                boolean preloadFailed = !preload();
                if (preloadFailed) return null;
            }
            do {
                pos = rnd.nextInt(size);
            } while (!displayed.add(pos));
            cids[i] = catalogs.get(pos).getCid();
        }
        return batchGetFromDynamo(cids);
    }

    private boolean preload() {
        List<Catalog> c = crudService.randomBuffer();
        if (c != null) {
            catalogs = c;
            return true;
        }
        return false;
    }

    private Panel[] batchGetFromDynamo(Integer[] cids) {
        return (Panel[]) Dynamo.helper().batchGetAsJson(cids).stream().map(Panel::new).toArray();
    }
}
