package photon.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import photon.mfw.model.Panel;
import photon.mfw.model.Catalog;
import photon.mfw.service.CrudService;
import photon.mfw.service.GalleryService;

@RestController
@RequestMapping("/api/mfw")
public class MfwApiController {

    private CrudService crud;
    private GalleryService gallery;

    @Autowired
    public MfwApiController(CrudService crud, GalleryService gallery) {
        this.crud = crud;
        this.gallery = gallery;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Catalog create(@ModelAttribute Catalog catalog) {
        return crud.put(catalog);
    }

    @RequestMapping(value = "/star", method = RequestMethod.POST)
    public boolean star(@RequestParam Integer articleId, @RequestParam Integer oid) {
        crud.markFavorite(articleId, oid, true);
        return true;
    }

    @RequestMapping(value = "/noshow", method = RequestMethod.POST)
    public boolean noshow(@RequestParam Integer articleId, @RequestParam Integer oid) {
        crud.noshow(articleId, oid);
        return true;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Panel[] getPanels(@RequestParam(required = false) Integer oid, @RequestParam(defaultValue = "4") int batchSize) {
        return gallery.nextBatch(oid, batchSize);
    }

    @RequestMapping("/init")
    public Integer init(@RequestParam(required = false) Integer oid) {
        if (oid != null) {
            new Thread(() -> gallery.init(oid)).start();
            return oid;
        }
        return 0;
    }
}
