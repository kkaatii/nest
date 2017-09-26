package photon.tube.query.search;

import photon.tube.auth.OafService;
import photon.tube.auth.UnauthorizedException;
import photon.tube.model.*;
import photon.tube.query.GraphContainer;
import photon.util.GenericDict;

import java.util.Arrays;

/**
 * Created by dan on 24/09/2017.
 */
public class MockSearcher extends Searcher {

    public MockSearcher(CrudService crudService, OafService oafService) {
        super(crudService, oafService);
    }

    @Override
    public GraphContainer search(Owner owner, GenericDict params)
            throws UnauthorizedException {
        Node n1 = new Node(1, "N1");
        Node n2 = new Node(2, "N2");
        Node n3 = new Node(3, "N3");
        Node n4 = new Node(4, "N4");
        Node n5 = new Node(5, "N5");
        Arrow a12 = new Arrow(n1, ArrowType.PARENT_OF, n2);
        Arrow a13 = new Arrow(n1, ArrowType.FOLDER_OF, n3);
        Arrow a24 = new Arrow(n2, ArrowType.PARENT_OF, n4);
        Arrow a45 = new Arrow(n4, ArrowType.PAIRING_WITH, n5);
        Point p1 = new Point(n1);
        Point p2 = new Point(n2);
        Point p3 = new Point(n3);
        Point p4 = new Point(n4);
        Point p5 = new Point(n5);

        GraphContainer gc = new GraphContainer();
        gc.add(p1);
        gc.addWithNextDepth(p2);
        gc.add(p3);
        gc.addWithNextDepth(p4);
        gc.addWithNextDepth(p5);
        gc.addArrow(Arrays.asList(a12, a13, a24, a45));

        return gc.sort();
    }
}
