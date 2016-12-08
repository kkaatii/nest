package photon.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import photon.tube.model.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tube")
public class CrudApiController {

    private final CrudService cs;

    @Autowired
    public CrudApiController(CrudService cs) {
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

    @RequestMapping(value = "/node-get")
    public Node get(@RequestParam Integer nid) {
        return cs.getNode(nid);
    }

    @RequestMapping(value = "/node-create", method = RequestMethod.POST)
    public Node create(@RequestBody Node n) {
        cs.putNode(n);
        return n;
    }

    @RequestMapping(value = "/node-update", method = RequestMethod.POST)
    public Node update(@RequestBody Node n) {
        return cs.updateNode(n);
    }

    @RequestMapping(value = "/node-activate", method = RequestMethod.POST)
    public String activate(@RequestParam Integer nid, @RequestParam boolean a) {
        cs.activateNode(nid, a);
        return cs.getNode(nid).isActive() == a ? "Success" : "Failure";
    }

    @RequestMapping("/point-get-frame")
    public List<Point> getFrame(@RequestParam String frame) {
        return cs.getAllPointsFromFrame(frame);
    }

    @RequestMapping("/pointmap-get-owner")
    public Map<Integer, Point> getPointMapByOwner(@RequestParam Integer oid) {
        return cs.getPointMapOwnedBy(oid);
    }

    @RequestMapping("/point-get-owner")
    public List<Point> getPointByOwner(@RequestParam Integer oid) {
        return cs.getPointsOwnedBy(oid);
    }

    @RequestMapping("/point-get")
    public Point getPoint(@RequestParam Integer pid) {
        return cs.getPoint(pid);
    }

    @RequestMapping("/point-get-multiple")
    public List<Point> getPoints(@RequestParam Integer[] pid) {
        return cs.getPoints(Arrays.asList(pid));
    }

    @RequestMapping("/point-map-get")
    public Map<Integer, Point> getPointMap(@RequestParam Integer[] pid) {
        return cs.getPointMap(Arrays.asList(pid));
    }

    @RequestMapping(value = "/arrow-create", method = RequestMethod.POST)
    public void createArrow(@RequestParam Integer[] nid, @RequestParam ArrowType at) {
        cs.putArrow(new Arrow(nid[0], at, nid[1]));
    }

    @RequestMapping("/arrows-between")
    public List<FrameArrow> arrowsBetween(@RequestParam Integer f, @RequestParam Integer t) {
        return cs.getAllArrowsBetween(f, t);
    }

    @RequestMapping("/arrow-delete")
    public void delete(@RequestParam Integer f, @RequestParam ArrowType at, @RequestParam Integer t) {
        cs.deleteArrow(new Arrow(f, at, t));
    }

}
