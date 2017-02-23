package photon.tube.action.searcher;

import photon.tube.auth.OafService;
import photon.tube.auth.UnauthorizedActionException;
import photon.tube.model.*;
import photon.tube.query.SortedGraphContainer;
import photon.util.PQueue;

import java.util.*;

import static photon.tube.query.SortedGraphContainer.INIT_DEPTH;
import static photon.tube.auth.AccessLevel.READ;

public class ChainSearcher extends Searcher {

    public ChainSearcher(CrudService crudService, OafService oafService) {
        super(crudService, oafService);
    }

    @Override
    public SortedGraphContainer search(Owner owner, Object... args)
            throws GraphSearchArgumentClassException, UnauthorizedActionException {
        try {
            Integer[] origins = (Integer[]) args[0];
            ArrowType at = (ArrowType) args[1];

            PQueue<Integer> queue = new PQueue<>();
            Set<Arrow> arrowSet = new HashSet<>();
            Map<Integer, Integer> nodeIdToDepth = new HashMap<>();
            for (Integer origin : origins) {
                if (!oafService.authorized(READ, owner, crudService.getNodeFrame(origin)))
                    throw new UnauthorizedActionException();
                nodeIdToDepth.put(origin, INIT_DEPTH);
                queue.enqueue(origin);
            }

            int newOrigin, originDepth, candidate;
            Integer candidateDepth;
            List<FrameArrow> arrows;
            while (!queue.isEmpty()) {
                newOrigin = queue.dequeue();
                originDepth = nodeIdToDepth.get(newOrigin);
                arrows = crudService.getAllArrowsStartingFrom(newOrigin, at);
                for (FrameArrow a : arrows) {
                    if (!oafService.authorized(READ, owner, a.getTargetFrame())) continue;
                    candidate = a.getTarget();
                    candidateDepth = nodeIdToDepth.get(candidate);
                    if (candidateDepth == null) {
                        nodeIdToDepth.put(candidate, originDepth + 1);
                        queue.enqueue(candidate);
                        arrowSet.add(a);
                    } else {
                        arrowSet.add(a.reverse());
                    }
                }
            }

            SortedGraphContainer gc = new SortedGraphContainer();
            Map<Integer, Point> pointMap = crudService.getPointMap(nodeIdToDepth.keySet());
            pointMap.forEach((id, point) -> gc.add(point, nodeIdToDepth.get(id)));
            gc.addArrow(arrowSet);
            return gc.sort();

        } catch (ClassCastException e) {
            throw new GraphSearchArgumentClassException();
        }
    }
}
