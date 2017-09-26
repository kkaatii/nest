package photon.rest;

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
    public boolean star(@RequestParam Integer articleId, @RequestParam Integer _oid) {
        crud.markFavorite(articleId, _oid, true);
        return true;
    }

    @RequestMapping(value = "/noshow", method = RequestMethod.POST)
    public boolean noshow(@RequestParam Integer articleId, @RequestParam Integer _oid) {
        crud.noshow(articleId, _oid);
        return true;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Panel[] getPanels(@RequestParam Integer _oid, @RequestParam(defaultValue = "4") int batchSize) {
        return gallery.nextBatch(_oid, batchSize);
    }

    @RequestMapping("/init")
    public int init(@RequestParam Integer _oid) {
        if (_oid != null) {
            new Thread(() -> gallery.init(_oid)).start();
            return _oid;
        }
        return 0;
    }
}
