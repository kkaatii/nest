package photon.api;

import photon.data.Arrow;
import photon.data.ArrowType;
import photon.data.Node;
import photon.data.NodeType;
import photon.service.CrudService;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/test")
public class TestController {

    private CrudService cs;

    @Autowired
    public TestController(CrudService cs) {
        this.cs = cs;
    }

    @RequestMapping("/setup")
    public
    @ResponseBody
    String setup(Model model) {
        Node[] nodes = new Node[100];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = new Node("Node" + i, NodeType.NODE);
            cs.putNode(nodes[i]);
        }
        for (int i = 0; i < nodes.length; i++) {
            for (int j = i + 1; j < nodes.length; j++) {
                cs.putArrow(new Arrow(nodes[i].getId(), ArrowType.TYPE, nodes[j].getId()));
            }
        }
        return "success!";
    }

    /*
        @RequestMapping("/{qid}/sibling")
        public String sibling(@PathVariable Integer qid, Model model) {
            GraphContainer graphSequencer = gs.sibling(qid, ArrowType.TAGGED_BY);
            List<Arrow> arrows = graphSequencer.sliceByRank(0, 2).arrows();
            StringBuilder sb = new StringBuilder();
            arrows.forEach(a -> sb.append(a).append('\n'));
            model.addAttribute("name", sb.toString());
            return "test";
        }

        @RequestMapping("/{qid}/radiant")
        public String radiant(@PathVariable Integer qid, Model model) {
            GraphContainer graphSequencer = gs.radiant(qid, ArrowType.unspecified);
            List<Arrow> arrows = graphSequencer.sliceByRank(0, 0).arrows();
            StringBuilder sb = new StringBuilder();
            arrows.forEach(a -> sb.append(a).append('\n'));
            model.addAttribute("name", "" + graphSequencer.sliceByDepth(0, 1).points().get(0).getId()
                    + graphSequencer.sliceByDepth(0, 1, false, true).points().get(2).getId());
            return "test";
        }

        @RequestMapping("/{qid}/chain")
        public String chain(@PathVariable Integer qid, Model model) {
            GraphContainer graphSequencer = gs.chain(qid, ArrowType.TAGGED_BY);
            List<Arrow> arrows = graphSequencer.sliceByDepth(0, 1).arrows();
            StringBuilder sb = new StringBuilder();
            arrows.forEach(a -> sb.append(a).append('\n'));
            model.addAttribute("name", sb.toString());
            return "test";
        }

        @RequestMapping("/testactivate")
        public String testactivate(@RequestParam(value = "qid") Integer qid, Model model) {
            cs.updateNodeState(qid, NodeState.ACTIVE);
            model.addAttribute("name", cs.getNode(qid).getName());
            return "edit";
        }
    */
    @RequestMapping("/test")
    public String test(Model model) {
        //List<Point> dns = ds.previewInactive();
        //Chain node = gs.getNode(1121);
        //data.addAttribute("name", (node == null) ? 1 : node);
        return "home";
    }
}
