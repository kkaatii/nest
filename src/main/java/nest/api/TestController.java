package nest.api;

import nest.data.Arrow;
import nest.data.ArrowType;
import nest.data.Node;
import nest.data.NodeType;
import nest.service.CrudService;
import nest.service.QueryService;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    private CrudService cs;

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
                cs.putArrow(new Arrow(nodes[i].getId(), ArrowType.depending_on, nodes[j].getId()));
            }
        }
        return "success!";
    }

    /*
        @RequestMapping("/{id}/sibling")
        public String sibling(@PathVariable Integer id, Model model) {
            GraphContainer graphSequencer = gs.sibling(id, ArrowType.tagged_by);
            List<Arrow> arrows = graphSequencer.sliceByRank(0, 2).arrows();
            StringBuilder sb = new StringBuilder();
            arrows.forEach(a -> sb.append(a).append('\n'));
            model.addAttribute("name", sb.toString());
            return "test";
        }

        @RequestMapping("/{id}/radiant")
        public String radiant(@PathVariable Integer id, Model model) {
            GraphContainer graphSequencer = gs.radiant(id, ArrowType.unspecified);
            List<Arrow> arrows = graphSequencer.sliceByRank(0, 0).arrows();
            StringBuilder sb = new StringBuilder();
            arrows.forEach(a -> sb.append(a).append('\n'));
            model.addAttribute("name", "" + graphSequencer.sliceByDepth(0, 1).points().get(0).getId()
                    + graphSequencer.sliceByDepth(0, 1, false, true).points().get(2).getId());
            return "test";
        }

        @RequestMapping("/{id}/chain")
        public String chain(@PathVariable Integer id, Model model) {
            GraphContainer graphSequencer = gs.chain(id, ArrowType.tagged_by);
            List<Arrow> arrows = graphSequencer.sliceByDepth(0, 1).arrows();
            StringBuilder sb = new StringBuilder();
            arrows.forEach(a -> sb.append(a).append('\n'));
            model.addAttribute("name", sb.toString());
            return "test";
        }

        @RequestMapping("/testactivate")
        public String testactivate(@RequestParam(value = "id") Integer id, Model model) {
            cs.updateNodeState(id, NodeState.ACTIVE);
            model.addAttribute("name", cs.getNode(id).getName());
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
