package photon.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import photon.gallery.Panel;
import photon.model.Catalog;
import photon.service.MfwCrudService;
import photon.service.GalleryService;

@RestController
@RequestMapping("/mfw")
public class MfwApiController {

    private MfwCrudService mfwCrud;
    private GalleryService gallery;

    @Autowired
    public MfwApiController(MfwCrudService mfwCrud, GalleryService gallery) {
        this.mfwCrud = mfwCrud;
        this.gallery = gallery;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public
    @ResponseBody
    Catalog create(@ModelAttribute Catalog catalog) {
        return mfwCrud.put(catalog);
    }

    @RequestMapping(value = "/mark", method = RequestMethod.POST)
    public void mark(@RequestParam Integer articleId) {
        mfwCrud.markFavorite(articleId, true);
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
