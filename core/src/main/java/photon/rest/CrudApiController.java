package photon.rest;

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
    public List<Point> getPointInFrame(@RequestParam String frame) {
        return cs.listPointsInFrame(frame);
    }

    @RequestMapping("/pointmap-get-owner")
    public Map<Integer, Point> getPointMapByOwner(@RequestParam Integer _oid) {
        return cs.pointMapOwnedBy(_oid);
    }

    @RequestMapping("/point-get-owner")
    public List<Point> getPointByOwner(@RequestParam Integer _oid) {
        return cs.listPointsOwnedBy(_oid);
    }

    @RequestMapping("/point-get")
    public Point getPoint(@RequestParam Integer pid) {
        return cs.getPoint(pid);
    }

    @RequestMapping("/point-get-multiple")
    public List<Point> getPoints(@RequestParam Integer[] pid) {
        return cs.listPoints(Arrays.asList(pid));
    }

    @RequestMapping("/point-map-get")
    public Map<Integer, Point> getPointMap(@RequestParam Integer[] pid) {
        return cs.pointMapOf(Arrays.asList(pid));
    }

    @RequestMapping(value = "/arrow-create", method = RequestMethod.POST)
    public Arrow createArrow(@RequestParam Integer[] nid, @RequestParam ArrowType at) {
        Arrow a = new Arrow(nid[0], at, nid[1]);
        cs.putArrow(a);
        return a;
    }

    @RequestMapping("/arrows-between")
    public List<FrameArrow> arrowsBetween(@RequestParam Integer f, @RequestParam Integer t) {
        return cs.listFrameArrowsBetween(f, t);
    }

    @RequestMapping("/arrow-delete")
    public void delete(@RequestParam Integer f, @RequestParam ArrowType at, @RequestParam Integer t) {
        cs.deleteArrow(new Arrow(f, at, t));
    }

}
