package photon.tube.query.processor;


import photon.tube.model.Arrow;
import photon.tube.model.ArrowType;
import photon.tube.model.Point;
import photon.tube.query.ArgumentClassMismatchException;
import photon.tube.query.GraphContainer;
import photon.tube.service.CrudService;
import photon.util.EQueue;

import java.util.*;

import static photon.tube.query.GraphContainer.INIT_DEPTH;

/**
 * Created by Dun Liu on 5/28/2016.
 */

// TODO need reexamine correctness of the code
public class ChainProcessor implements Processor {

    private CrudService crudService;

    public ChainProcessor(CrudService crudService) {
        this.crudService = crudService;
    }

    @Override
    public GraphContainer process(Object... args) throws ArgumentClassMismatchException {
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
                List<Arrow> arrows = crudService.getAllArrowsStartingFrom(newOrigin, at);
                for (Arrow a : arrows) {
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
            throw new ArgumentClassMismatchException();
        }
    }
}
