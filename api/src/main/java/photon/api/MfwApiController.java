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

    @RequestMapping(value = "/mark", method = RequestMethod.POST)
    public void mark(@RequestParam Integer articleId) {
        mfwCrud.markFavorite(articleId, true);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Panel[] getPanels(@RequestParam(required = false) Integer userId, @RequestParam(defaultValue = "4") int batchSize) {
        return gallery.nextBatch(userId == null ? User.DEFAULT_USER_ID : userId, batchSize);
    }

    @RequestMapping("/init")
    public void init(@RequestParam(required = false) Integer userId) {
        gallery.init(userId == null ? User.DEFAULT_USER_ID : userId);
    }

}
