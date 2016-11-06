package photon.service;

import photon.model.mfw.Panel;

public interface MfwGalleryService {

    void init(Integer userId);

    Panel[] nextBatch(Integer userId, int batchSize);
}
