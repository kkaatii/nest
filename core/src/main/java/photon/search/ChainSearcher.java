package photon.search;

import photon.crud.OafService;
import photon.crud.UnauthorizedException;
import photon.model.*;
import photon.query.GraphContainer;
import photon.crud.CrudService;
import photon.util.PQueue;
import photon.util.GenericDict;

import java.util.*;

import static photon.query.GraphContainer.INIT_DEPTH;
import static photon.crud.AccessLevel.READ;

class ChainSearcher extends Searcher {

    public ChainSearcher(CrudService crudService, OafService oafService) {
        super(crudService, oafService);
    }

    @Override
    public GraphContainer search(Owner owner, GenericDict params)
            throws UnauthorizedException {
        int[] origins = params.get("origins", int[].class);
        ArrowType at = ArrowType.extendedValueOf(params.get("arrow_type", String.class));

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
        return gc;
    }
}
