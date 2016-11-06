package photon.mfw.service;

import photon.mfw.model.Panel;

public interface GalleryService {

    void init(Integer userId);

    Panel[] nextBatch(Integer userId, int batchSize);
}
