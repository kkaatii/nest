package photon.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import photon.gallery.MfwGalleryService;
import photon.gallery.Panel;

/**
 * Created by dan on 07/10/2016.
 */

@RestController
@RequestMapping("/api/mfw")
public class MfwApiController {
    private MfwGalleryService gallery;

    @Autowired
    public MfwApiController(MfwGalleryService gallery) {
        this.gallery = gallery;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Panel[] getPanels(@RequestParam(defaultValue = "4") int batchSize) {
        return gallery.nextBatch(batchSize);
    }

    @RequestMapping("/init")
    public boolean init() {
        return gallery.init();
    }
}
