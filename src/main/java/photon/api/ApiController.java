package photon.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import photon.service.GalleryService;
import photon.gallery.Panel;

@RestController
@RequestMapping("/api/mfw")
public class ApiController {
    private GalleryService gallery;

    @Autowired
    public ApiController(GalleryService gallery) {
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