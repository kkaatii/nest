package photon.service;

import photon.gallery.Panel;
import photon.gallery.PanelId;

/**
 * Created by dan on 07/10/2016.
 */
public interface GalleryService {
    boolean init();
    Panel[] nextBatch(int batchSize);
    boolean permanentRemove(PanelId panelId);
}
