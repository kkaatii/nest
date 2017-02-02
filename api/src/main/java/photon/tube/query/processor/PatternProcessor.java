package photon.tube.query.processor;

import photon.tube.auth.OafService;
import photon.tube.auth.UnauthorizedActionException;
import photon.tube.model.*;
import photon.tube.query.GraphContainer;
import photon.tube.query.pattern.MatchingRecord;
import photon.tube.query.pattern.Pattern;
import photon.tube.query.pattern.PatternSegment;
import photon.util.ImmutableTuple;
import photon.util.PStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static photon.tube.auth.AccessLevel.READ;
import static photon.tube.query.GraphContainer.INIT_DEPTH;

public class PatternProcessor extends Processor {

    public PatternProcessor(CrudService crudService, OafService oafService) {
        super(crudService, oafService);
    }

    @Override
    public GraphContainer process(Owner owner, Object... args)
            throws QueryArgumentClassException, UnauthorizedActionException {
        try {
            Integer[] origins = (Integer[]) args[0];
            String[] seqString = (String[]) args[1];
            if (origins.length == 0) {
                return GraphContainer.emptyContainer();
            }

            GraphContainer gc = new GraphContainer();

            if (seqString.length == 0) {
                List<Point> points = crudService.getPoints(Arrays.asList(origins));
                points.forEach(point -> {
                    if (!oafService.authorized(READ, owner, point.getFrame())) {
                        throw new UnauthorizedActionException();
                    }
                });
                gc.addAll(points);
                return gc;
            }

            Pattern<ArrowType> pattern = new Pattern<>();
            Map<Integer, Integer> nodeIdToDepth = new HashMap<>();
            Map<ImmutableTuple<Integer, PatternSegment<ArrowType>>, MatchingRecord<ArrowType>> recordMap = new HashMap<>();
            PStack<MatchingRecord<ArrowType>> stack = new PStack<>();
            for (int i = 0; i < seqString.length; i++) {
                String s, n;
                if (i + 1 == seqString.length || Character.isLetter(seqString[i + 1].charAt(0))) {
                    s = seqString[i];
                    n = "1";
                } else {
                    s = seqString[i++];
                    n = seqString[i];
                }
                ArrowType unit = s.startsWith(ArrowType.REVERSE_SIGN)
                        ? ArrowType.valueOf(s.substring(1)).reverse()
                        : ArrowType.valueOf(s);
                pattern.append(unit, n);
            }

            for (Integer origin : origins) {
                // If any of the queried origins is not readable then it must be an illegal query request thus an
                // exception is thrown
                if (!oafService.authorized(READ, owner, crudService.getNodeFrame(origin))) {
                    throw new UnauthorizedActionException();
                }
                pattern.first(nextSeg -> {
                    ImmutableTuple<Integer, PatternSegment<ArrowType>> rKey =
                            new ImmutableTuple<>(origin, nextSeg);
                    MatchingRecord<ArrowType> r = new MatchingRecord<>(origin, INIT_DEPTH, nextSeg);
                    recordMap.put(rKey, r);
                    stack.push(r);
                });
            }

            PStack<MatchingRecord<ArrowType>> ancestors = new PStack<>();
            while (!stack.isEmpty()) {
                MatchingRecord<ArrowType> record = stack.pop();
                int depth = record.depth;

                // When segment is null, it means that the record is already at the end of the pattern and shall be matched
                if (record.segment == null) {
                    ancestors.push(record);
                    continue;
                }

                List<FrameArrow> farrows = crudService.getAllArrowsStartingFrom(record.id, record.segment.unit);
                for (FrameArrow fa : farrows) {
                    if (!oafService.authorized(READ, owner, fa.getTargetFrame())) continue;
                    Integer candidate = fa.getTarget();
                    pattern.next(
                            record.segment,
                            nextSeg -> {
                                ImmutableTuple<Integer, PatternSegment<ArrowType>> rKey =
                                        new ImmutableTuple<>(candidate, nextSeg);
                                MatchingRecord<ArrowType> r = recordMap.get(rKey);
                                if (r != null) {
                                    r.parents.add(record);
                                    if (r.depth > depth + 1) {
                                        r.depth = depth + 1;
                                    }
                                } else {
                                    r = new MatchingRecord<>(candidate, depth + 1, nextSeg);
                                    r.parents.add(record);
                                    recordMap.put(rKey, r);
                                    stack.push(r);
                                }
                            }
                    );
                }
            }

            // Back-dyeing: traverse back from a matched node along the pattern
            while (!ancestors.isEmpty()) {
                MatchingRecord<ArrowType> r = ancestors.pop();
                Integer prevDepth = nodeIdToDepth.get(r.id);
                if (prevDepth != null) {
                    if (prevDepth > r.depth) {
                        nodeIdToDepth.put(r.id, r.depth);
                    }
                } else {
                    nodeIdToDepth.put(r.id, r.depth);
                    for (MatchingRecord<ArrowType> parent : r.parents) {
                        ancestors.push(parent);
                        gc.addArrow(new Arrow(parent.id, parent.segment.unit, r.id));
                    }
                }
            }

            Map<Integer, Point> pointMap = crudService.getPointMap(nodeIdToDepth.keySet());
            pointMap.forEach((id, point) -> gc.add(point, nodeIdToDepth.get(id)));
            return gc.sort();
        } catch (ClassCastException cce) {
            throw new QueryArgumentClassException();
        }
    }

}
