package photon.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import photon.gallery.Panel;
import photon.model.Catalog;
import photon.model.User;
import photon.service.MfwCrudService;
import photon.service.GalleryService;

@RestController
@RequestMapping("/api/mfw")
public class MfwApiController {

    private MfwCrudService mfwCrud;
    private GalleryService gallery;

    @Autowired
    public MfwApiController(MfwCrudService mfwCrud, GalleryService gallery) {
        this.mfwCrud = mfwCrud;
        this.gallery = gallery;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Catalog create(@ModelAttribute Catalog catalog) {
        return mfwCrud.put(catalog);
    }

    @RequestMapping(value = "/star", method = RequestMethod.POST)
    public boolean star(@RequestParam Integer articleId, @RequestParam String name) {
        mfwCrud.markFavorite(articleId, name, true);
        return true;
    }

    @RequestMapping(value = "/noshow", method = RequestMethod.POST)
    public boolean noshow(@RequestParam Integer articleId, @RequestParam String name) {
        mfwCrud.noshow(articleId, name);
        return true;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Panel[] getPanels(@RequestParam(required = false) String name, @RequestParam(defaultValue = "4") int batchSize) {
        return gallery.nextBatch(mfwCrud.findUserId(name), batchSize);
    }

    @RequestMapping("/init")
    public Integer init(@RequestParam(required = false) String name) {
        Integer id = mfwCrud.findUserId(name);
        if (id == null) return null;
        new Thread(() -> gallery.init(id)).start();
        return id;
    }

}
