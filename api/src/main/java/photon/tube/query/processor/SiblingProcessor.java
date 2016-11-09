package photon.tube.query.processor;

import photon.tube.model.Arrow;
import photon.tube.model.ArrowType;
import photon.tube.query.ArgumentClassMismatchException;
import photon.tube.query.GraphContainer;
import photon.tube.service.CrudService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SiblingProcessor implements Processor {

    private CrudService crudService;

    public SiblingProcessor(CrudService crudService) {
        this.crudService = crudService;
    }

    @Override
    public GraphContainer process(Object... args) throws ArgumentClassMismatchException {
        try {
            int[] origins = (int[]) args[0];
            if (origins.length == 0) return GraphContainer.emptyInstance();
            ArrowType at = (ArrowType) args[1];

            GraphContainer gc = new GraphContainer();
            List<Arrow> al = new ArrayList<>();
            Set<Integer> nodeIdSet = new HashSet<>();
            for (int origin : origins) {
                gc.add(crudService.getPoint(origin));
                List<Arrow> bridgeArrows = crudService.getAllArrowsStartingFrom(origin, at);
                if (bridgeArrows.isEmpty())
                    continue;
                for (Arrow a : bridgeArrows) {
                    int bridgeNodeId = a.getTarget();
                    al.addAll(crudService.getAllArrowsStartingFrom(bridgeNodeId, at.reverse()));
                    nodeIdSet.add(a.getTarget());
                }
                for (Arrow a : al) {
                    if (a.getTarget() == origin)
                        continue;
                    nodeIdSet.add(a.getTarget());
                }
            }
            if (!nodeIdSet.isEmpty())
                gc.addAllWithNextDepth(crudService.getPoints(nodeIdSet));
            if (!al.isEmpty())
                gc.addArrow(al);
            return gc.organize();

        } catch (ClassCastException e) {
            throw new ArgumentClassMismatchException();
        }
    }
}
