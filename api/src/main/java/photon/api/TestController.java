package photon.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import photon.tube.model.*;
import photon.tube.service.CrudService;
import photon.tube.service.QueryService;

import java.util.*;

@RestController
@RequestMapping("/api/q/test")
public class TestController {

    private final CrudService cs;

    @Autowired
    public TestController(CrudService cs) {
        this.cs = cs;
    }

    @RequestMapping(value = "/node-batch-create", method = RequestMethod.POST)
    public Node batchCreate() {
        Node n = null;
        for (int i = 0; i < 30; i++) {
            n = new Node("#" + i + "Hello, World!");
            n.setOwnerId(1);
            cs.putNode(n);
        }
        return n;
    }

    @RequestMapping(value = "/node-create", method = RequestMethod.POST)
    public Node create(@RequestBody Node n) {
        cs.putNode(n);
        return n;
    }

    @RequestMapping(value = "/node-update", method = RequestMethod.POST)
    public Node update(@RequestParam Integer id) {
        Node n = new Node(NodeType.TAG, new Date().toString());
        n.setId(id);
        n.setOwnerId(1);
        cs.putNode(n);
        return n;
    }

    @RequestMapping(value = "/node-deactivate", method = RequestMethod.POST)
    public String deactivate(@RequestParam Integer id) {
        cs.activateNode(id, false);
        return cs.getNode(id).isActive() ? "Failure" : "Success";
    }

    @RequestMapping(value = "/node-reactivate", method = RequestMethod.POST)
    public String reactivate(@RequestParam Integer id) {
        cs.activateNode(id, true);
        return cs.getNode(id).isActive() ? "Success" : "Failure";
    }

    @RequestMapping("/node-get")
    public Node get(@RequestParam Integer id) {
        return cs.getNode(id);
    }

    @RequestMapping("/point-get")
    public Point getPoint(@RequestParam Integer id) {
        return cs.getPoint(id);
    }

    @RequestMapping("/point-get-multiple")
    public List<Point> getPoints(@RequestParam Integer[] ids) {
        return cs.getPoints(Arrays.asList(ids));
    }

    @RequestMapping("/point-map-get")
    public Map<Integer, Point> getPointMap(@RequestParam Integer[] ids) {
        return cs.getPointMap(Arrays.asList(ids));
    }

    @RequestMapping(value="/arrow-create", method=RequestMethod.POST)
    public void createArrow(@RequestParam Integer[] ids) {
        for (int i = 0; i < ids.length-1; i++) {
            for (int j = i+1; j < ids.length; j++) {
                cs.putArrow(new Arrow(ids[i], ArrowType.PARENT_OF, ids[j]));
            }
        }
    }

    @RequestMapping("/arrows-between")
    public List<Arrow> arrowsBetween(@RequestParam Integer f, @RequestParam Integer t) {
        return cs.getAllArrowsBetween(f, t);
    }

    @RequestMapping("/arrow-delete")
    public void delete(@RequestParam Integer f, @RequestParam ArrowType at, @RequestParam Integer t) {
        cs.deleteArrow(new Arrow(f, at, t));
    }

}
