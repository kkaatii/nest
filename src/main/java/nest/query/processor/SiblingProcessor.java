package nest.query.processor;

import nest.data.Arrow;
import nest.data.ArrowType;
import nest.query.ArgumentClassMismatchException;
import nest.query.GraphContainer;
import nest.query.Processor;
import nest.service.CrudService;

import java.util.*;

public class SiblingProcessor implements Processor {

    private CrudService crudService;

    public SiblingProcessor(CrudService crudService) {
        this.crudService = crudService;
    }

    @Override
    public GraphContainer execute(Object... args) throws ArgumentClassMismatchException {
        try {
            int[] origins = (int[]) args[0];
            if (origins.length == 0) return GraphContainer.emptyInstance();
            ArrowType at = (ArrowType) args[1];

            GraphContainer gc = new GraphContainer();
            List<Arrow> al = new ArrayList<>();
            Set<Integer> nodeIdSet = new HashSet<>();
            Set<Integer> extIdSet = new HashSet<>();
            for (int origin : origins) {
                gc.add(crudService.getPoint(origin));
                List<Arrow> bridgeArrows = crudService.getAllArrowsOriginatingFrom(origin, at);
                if (bridgeArrows.isEmpty())
                    continue;
                for (Arrow a : bridgeArrows) {
                    int bridgeNodeId = a.getTarget();
                    al.addAll(crudService.getAllArrowsOriginatingFrom(bridgeNodeId, at.reverse()));
                    nodeIdSet.add(a.getTarget());
                }
                for (Arrow a : al) {
                    if (a.getTarget() == origin)
                        continue;
                    nodeIdSet.add(a.getTarget());
                    if (a.hasExtension())
                        extIdSet.add(a.getExtension());
                }
            }
            if (!nodeIdSet.isEmpty())
                gc.addAllNextDepth(crudService.getPoints(nodeIdSet));
            if (!al.isEmpty())
                gc.addArrow(al);
            if (!extIdSet.isEmpty())
                gc.addExtension(crudService.getExtensions(extIdSet));
            return gc.organize();

        } catch (ClassCastException e) {
            throw new ArgumentClassMismatchException();
        }
    }
}
