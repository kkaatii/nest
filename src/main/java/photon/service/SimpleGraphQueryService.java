package photon.service;

/**
 * Implements the following functions: <p>
 * + BFS-based <tt>chain</tt> view along a specific <tt>Arrow.Type</tt> chain <p>
 */
//@Service
public class SimpleGraphQueryService {
/*
    @Autowired
    private CrudService crudService;

    
    public GraphContainer complete(int... ids) {
        List<Arrow> arrows = new ArrayList<>();
        for (int i = 0; i < ids.length; i++) {
            for (int j = i + 1; j < ids.length; j++) {
                arrows.addAll(crudService.getAllArrowsBetween(ids[i], ids[j]));
            }
        }
        Set<Integer> extIdSet = arrows.stream()
                .filter(Arrow::hasExtension)
                .map(Arrow::getExtension)
                .collect(Collectors.toSet());
        return GraphContainer.fixateWith(Util.ensureList(null), arrows, crudService.getExtensions(extIdSet));
    }

    
    public GraphContainer sibling(int origin, ArrowType at) {
        List<Arrow> bridgeArrows = crudService.getAllArrowsOriginatingFrom(origin, at);
        if (bridgeArrows.isEmpty())
            return GraphContainer.emptyInstance();
        List<Arrow> al = new ArrayList<>();
        Set<Integer> nodeIdSet = new HashSet<>();
        for (Arrow a : bridgeArrows) {
            int bridgeNodeId = a.getTarget();
            al.addAll(crudService.getAllArrowsOriginatingFrom(bridgeNodeId, at.reverse()));
            nodeIdSet.add(a.getTarget());
        }
        Set<Integer> extIdSet = new HashSet<>();
        for (Arrow a : al) {
            if (a.getTarget() == origin)
                continue;
            nodeIdSet.add(a.getTarget());
            if (a.hasExtension())
                extIdSet.add(a.getExtension());
        }
        GraphContainer g = new GraphContainer();
        g.add(crudService.getPoint(origin));
        g.addAllNextDepth(crudService.getPoints(nodeIdSet));
        g.addArrow(al);
        g.addExtension(crudService.getExtensions(extIdSet));
        return g.organize();
    }

    public GraphContainer radiant(int origin, ArrowType at) {
        List<Arrow> al = crudService.getAllArrowsOriginatingFrom(origin, at);
        if (al.isEmpty())
            return GraphContainer.emptyInstance();
        Set<Integer> extIdSet = new HashSet<>();
        Set<Integer> nodeIdSet = new HashSet<>();
        for (Arrow a : al) {
            if (a.hasExtension())
                extIdSet.add(a.getExtension());
            nodeIdSet.add(a.getTarget());
        }
        GraphContainer g = new GraphContainer();
        g.add(crudService.getPoint(origin));
        g.addAllNextDepth(crudService.getPoints(nodeIdSet));
        g.addArrow(al);
        g.addExtension(crudService.getExtensions(extIdSet));
        return g.organize();
    }

    
    public GraphContainer chain(int origin, ArrowType at) {
        EQueue<Integer> queue = new EQueue<>();
        Set<Integer> extIdSet = new HashSet<>();
        Map<Integer, Integer> nodeIdToDepth = new HashMap<>();
        GraphContainer graphSequencer = new GraphContainer();
        nodeIdToDepth.put(origin, graphSequencer.minDepth());
        queue.enqueue(origin);

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
                } /* else if (candidateDepth > originDepth + 1) {
                    nodeIdToDepth.put(candidate, originDepth + 1);
                } *//*
                if (a.hasExtension())
                    extIdSet.add(a.getExtension());
                graphSequencer.addArrow(a);
            }
        }

        Map<Integer, Point> pointMap = crudService.getPointMap(nodeIdToDepth.keySet());
        pointMap.forEach((qid, point) -> graphSequencer.add(point, nodeIdToDepth.get(qid)));
        graphSequencer.addExtension(crudService.getExtensions(extIdSet));
        return graphSequencer.organize();
    }
*/
}
