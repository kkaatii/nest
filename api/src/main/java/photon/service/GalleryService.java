package photon.service;

import photon.gallery.Panel;

public interface GalleryService {

    boolean init();

    Panel[] nextBatch(int batchSize);
}
