package photon.service;

import photon.gallery.Panel;

public interface GalleryService {

    void init(Integer userId);

    Panel[] nextBatch(Integer userId, int batchSize);
}
