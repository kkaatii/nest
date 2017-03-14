package photon.tube.action.search;

import photon.tube.auth.OafService;
import photon.tube.auth.UnauthorizedQueryException;
import photon.tube.model.*;
import photon.tube.graph.SortedGraphContainer;
import photon.util.PQueue;
import photon.util.GenericDict;

import java.util.*;

import static photon.tube.graph.SortedGraphContainer.INIT_DEPTH;
import static photon.tube.auth.AccessLevel.READ;

public class ChainSearcher extends Searcher {

    public ChainSearcher(CrudService crudService, OafService oafService) {
        super(crudService, oafService);
    }

    @Override
    public SortedGraphContainer search(Owner owner, GenericDict params)
            throws GraphSearchArgumentClassException, UnauthorizedQueryException {
        try {
            int[] origins = params.get(int[].class, "origins");
            ArrowType at = params.get(ArrowType.class, "arrow_type");

            PQueue<Integer> queue = new PQueue<>();
            Set<Arrow> arrowSet = new HashSet<>();
            Map<Integer, Integer> nodeIdToDepth = new HashMap<>();
            for (int origin : origins) {
                if (!oafService.authorized(READ, owner, crudService.getNodeFrame(origin)))
                    throw new UnauthorizedQueryException();
                nodeIdToDepth.put(origin, INIT_DEPTH);
                queue.enqueue(origin);
            }

            int newOrigin, originDepth, candidate;
            Integer candidateDepth;
            List<FrameArrow> arrows;
            while (!queue.isEmpty()) {
                newOrigin = queue.dequeue();
                originDepth = nodeIdToDepth.get(newOrigin);
                arrows = crudService.listArrowsStartingFrom(newOrigin, at);
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
            Map<Integer, Point> pointMap = crudService.pointMapOf(nodeIdToDepth.keySet());
            pointMap.forEach((id, point) -> gc.add(point, nodeIdToDepth.get(id)));
            gc.addArrow(arrowSet);
            return gc.sort();

        } catch (ClassCastException e) {
            throw new GraphSearchArgumentClassException();
        }
    }
}
