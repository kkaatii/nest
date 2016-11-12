package photon.tube.query.processor;


import photon.tube.model.*;
import photon.tube.query.QueryArgumentClassMismatchException;
import photon.tube.query.GraphContainer;
import photon.tube.service.CrudService;
import photon.tube.service.OafService;
import photon.util.EQueue;

import java.util.*;

import static photon.tube.query.GraphContainer.INIT_DEPTH;

public class ChainProcessor implements Processor {

    private final CrudService crudService;
    private final OafService oafService;

    public ChainProcessor(CrudService crudService, OafService oafService) {
        this.crudService = crudService;
        this.oafService = oafService;
    }

    @Override
    public GraphContainer process(Owner owner, Object... args) throws QueryArgumentClassMismatchException {
        try {
            Integer[] origins = (Integer[]) args[0];
            ArrowType at = (ArrowType) args[1];

            EQueue<Integer> queue = new EQueue<>();
            Set<Arrow> arrowSet = new HashSet<>();
            Map<Integer, Integer> nodeIdToDepth = new HashMap<>();
            for (Integer origin : origins) {
                nodeIdToDepth.put(origin, INIT_DEPTH);
                queue.enqueue(origin);
            }
            while (!queue.isEmpty()) {
                int newOrigin = queue.dequeue();
                int originDepth = nodeIdToDepth.get(newOrigin);
                List<FrameArrow> arrows = crudService.getAllArrowsStartingFrom(newOrigin, at);
                for (FrameArrow a : arrows) {
                    if (!oafService.authorizedRead(owner, a.getTargetFrame())) continue;
                    int candidate = a.getTarget();
                    Integer candidateDepth = nodeIdToDepth.get(candidate);
                    if (candidateDepth == null) {
                        nodeIdToDepth.put(candidate, originDepth + 1);
                        queue.enqueue(candidate);
                        arrowSet.add(a);
                    } else {arrowSet.add(a.reverse());}
                }
            }

            GraphContainer gc = new GraphContainer();
            Map<Integer, Point> pointMap = crudService.getPointMap(nodeIdToDepth.keySet());
            pointMap.forEach((id, point) -> gc.add(point, nodeIdToDepth.get(id)));
            gc.addArrow(arrowSet);
            return gc.organize();

        } catch (ClassCastException e) {
            throw new QueryArgumentClassMismatchException();
        }
    }
}
