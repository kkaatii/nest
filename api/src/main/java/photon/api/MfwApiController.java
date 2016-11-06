package photon.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import photon.model.mfw.Panel;
import photon.model.mfw.Catalog;
import photon.service.MfwCrudService;
import photon.service.MfwGalleryService;

@RestController
@RequestMapping("/api/mfw")
public class MfwApiController {

    private MfwCrudService crud;
    private MfwGalleryService gallery;

    @Autowired
    public MfwApiController(MfwCrudService crud, MfwGalleryService gallery) {
        this.crud = crud;
        this.gallery = gallery;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Catalog create(@ModelAttribute Catalog catalog) {
        return crud.put(catalog);
    }

    @RequestMapping(value = "/star", method = RequestMethod.POST)
    public boolean star(@RequestParam Integer articleId, @RequestParam String name) {
        crud.markFavorite(articleId, name, true);
        return true;
    }

    @RequestMapping(value = "/noshow", method = RequestMethod.POST)
    public boolean noshow(@RequestParam Integer articleId, @RequestParam String name) {
        crud.noshow(articleId, name);
        return true;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Panel[] getPanels(@RequestParam(required = false) String name, @RequestParam(defaultValue = "4") int batchSize) {
        return gallery.nextBatch(crud.findUserId(name), batchSize);
    }

    @RequestMapping("/init")
    public Integer init(@RequestParam(required = false) String name) {
        Integer id = crud.findUserId(name);
        if (id == null) return null;
        new Thread(() -> gallery.init(id)).start();
        return id;
    }

}
