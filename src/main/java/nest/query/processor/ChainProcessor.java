package nest.query.processor;

import nest.data.Arrow;
import nest.data.ArrowType;
import nest.data.Point;
import nest.query.ArgumentClassMismatchException;
import nest.query.GraphContainer;
import nest.query.Processor;
import nest.service.CrudService;
import nest.util.NQueue;

import java.util.*;

import static nest.query.GraphContainer.INIT_DEPTH;

/**
 * Created by Dun Liu on 5/28/2016.
 */
public class ChainProcessor implements Processor {

    private CrudService crudService;

    public ChainProcessor(CrudService crudService) {
        this.crudService = crudService;
    }

    @Override
    public GraphContainer process(Object... args) throws ArgumentClassMismatchException {
        try {
            int[] origins = (int[]) args[0];
            ArrowType at = (ArrowType) args[1];

            NQueue<Integer> queue = new NQueue<>();
            Set<Integer> extIdSet = new HashSet<>();
            Set<Arrow> arrowSet = new HashSet<>();
            Map<Integer, Integer> nodeIdToDepth = new HashMap<>();
            for (int origin : origins) {
                nodeIdToDepth.put(origin, INIT_DEPTH);
                queue.enqueue(origin);
            }
            while (!queue.isEmpty()) {
                int newOrigin = queue.dequeue();
                int originDepth = nodeIdToDepth.get(newOrigin);
                List<Arrow> arrows = crudService.getAllArrowsOriginatingFrom(newOrigin, at);
                for (Arrow a : arrows) {
                    int candidate = a.getTarget();
                    Integer candidateDepth = nodeIdToDepth.get(candidate);
                    if (candidateDepth == null) {
                        nodeIdToDepth.put(candidate, originDepth + 1);
                        queue.enqueue(candidate);
                        arrowSet.add(a);
                        if (a.hasExtension()) {
                            extIdSet.add(a.getExtension());
                        }
                    } else if (arrowSet.add(a.reverse()) && a.hasExtension())
                        extIdSet.add(a.getExtension());
                    /* else if (candidateDepth > originDepth + 1) {
                        nodeIdToDepth.put(candidate, originDepth + 1);
                    } */
                }
            }

            GraphContainer gc = new GraphContainer();
            Map<Integer, Point> pointMap = crudService.getPointMap(nodeIdToDepth.keySet());
            pointMap.forEach((id, point) -> gc.add(point, nodeIdToDepth.get(id)));
            gc.addArrow(arrowSet);
            gc.addExtension(crudService.getExtensions(extIdSet));
            return gc.organize();

        } catch (ClassCastException e) {
            throw new ArgumentClassMismatchException();
        }
    }
}
