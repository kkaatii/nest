package nest.api;

import nest.data.Node;
import nest.data.Point;
import nest.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/node")
public class NodeApiController {

    @Autowired
    private CrudService crudService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Node get(@PathVariable int id) {
        return crudService.getNode(id);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public Point create(@RequestBody Node node) {
        crudService.putNode(node);
        return new Point(node);
    }
}
