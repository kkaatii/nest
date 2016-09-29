package photon.api;

import photon.data.Arrow;
import photon.data.Extension;
import photon.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/arrow")
public class ArrowApiController {

    @Autowired
    private CrudService crudService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public List<Arrow> get(@PathVariable int[] id) {
        if (id.length != 2) {
            throw new NoSuchArrowException();
        }
        return crudService.getAllArrowsBetween(id[0], id[1]);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public int create(@RequestParam Arrow arrow, @RequestParam(defaultValue = "") Extension extension) {
        crudService.putArrow(arrow, extension);
        return arrow.getId();
    }
}
