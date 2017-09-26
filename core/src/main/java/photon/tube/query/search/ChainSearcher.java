package photon.tube.query.search;

import photon.tube.auth.OafService;
import photon.tube.auth.UnauthorizedException;
import photon.tube.model.*;
import photon.tube.query.GraphContainer;
import photon.util.PQueue;
import photon.util.GenericDict;

import java.util.*;

import static photon.tube.query.GraphContainer.INIT_DEPTH;
import static photon.tube.auth.AccessLevel.READ;

public class ChainSearcher extends Searcher {

    public ChainSearcher(CrudService crudService, OafService oafService) {
        super(crudService, oafService);
    }

    @Override
    public GraphContainer search(Owner owner, GenericDict params)
            throws UnauthorizedException {
        int[] origins = params.get(int[].class, "origins");
        ArrowType at = ArrowType.extendedValueOf(params.get(String.class, "arrow_type"));

        PQueue<Integer> queue = new PQueue<>();
        Set<Arrow> arrowSet = new HashSet<>();
        Map<Integer, Integer> nodeIdToDepth = new HashMap<>();
        for (int origin : origins) {
            if (!oafService.authorized(READ, owner, crudService.getNodeFrame(origin)))
                throw new UnauthorizedException();
            nodeIdToDepth.put(origin, INIT_DEPTH);
            queue.enqueue(origin);
        }

        int newOrigin, originDepth, candidate;
        Integer candidateDepth;
        List<FrameArrow> farrows;

        // BFS
        while (!queue.isEmpty()) {
            newOrigin = queue.dequeue();
            originDepth = nodeIdToDepth.get(newOrigin);
            farrows = crudService.listFrameArrowsStartingFrom(newOrigin, at);
            for (FrameArrow fa : farrows) {
                if (!oafService.authorized(READ, owner, fa.getTargetFrame())) continue;
                candidate = fa.getTarget();
                candidateDepth = nodeIdToDepth.get(candidate);
                if (candidateDepth == null) {
                    nodeIdToDepth.put(candidate, originDepth + 1);
                    queue.enqueue(candidate);
                    arrowSet.add(fa);
                } else {
                    arrowSet.add(fa.reverse());
                }
            }
        }

        GraphContainer gc = new GraphContainer();
        Map<Integer, Point> pointMap = crudService.pointMapOf(nodeIdToDepth.keySet());
        pointMap.forEach((id, point) -> gc.add(point, nodeIdToDepth.get(id)));
        gc.addArrow(arrowSet);
        return gc.sort();
    }
}
