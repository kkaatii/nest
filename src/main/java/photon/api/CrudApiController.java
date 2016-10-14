package photon.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import photon.model.Catalog;
import photon.model.ViewLog;
import photon.service.CrudService;

@RestController
@RequestMapping("/api/mfw")
public class CrudApiController {
    private CrudService crudService;

    @Autowired
    public CrudApiController(CrudService crudService) {
        this.crudService = crudService;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public void create(@RequestParam Catalog catalog) {
        crudService.put(catalog);
    }

    @RequestMapping(value="/mark", method = RequestMethod.POST)
    public void mark(@RequestParam Integer articleId) {
        crudService.markFavorite(articleId, true);
    }

}
