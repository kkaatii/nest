package photon.test;

import org.springframework.web.bind.annotation.RequestParam;
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
    String setup(@RequestParam(required = false) Integer depth, @RequestParam(required = false) Integer startId) {
        if (depth == null) {
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
            return "Success!";
        }

        TestSetGenerator tsg = TestSetGenerator.buildDefault(depth, (startId == null) ? 1 : startId);
        assert tsg != null;
        tsg.getNodes().forEach(node -> cs.putNode(node));
        tsg.getArrows().forEach(arrow -> cs.putArrow(arrow));

        return "Success";
    }

}
