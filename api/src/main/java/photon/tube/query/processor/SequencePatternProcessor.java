package photon.tube.query.processor;

import photon.tube.auth.OafService;
import photon.tube.auth.UnauthorizedActionException;
import photon.tube.model.*;
import photon.tube.query.SortedGraphContainer;
import photon.tube.query.pattern.MatchingRecord;
import photon.tube.query.pattern.SequencePattern;
import photon.tube.query.pattern.SequencePatternElement;
import photon.util.ImmutableTuple;
import photon.util.PStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static photon.tube.auth.AccessLevel.READ;
import static photon.tube.query.SortedGraphContainer.INIT_DEPTH;

public class SequencePatternProcessor extends Processor {

    private int MAX_QUERY_DEPTH = 255;

    public SequencePatternProcessor(CrudService crudService, OafService oafService) {
        super(crudService, oafService);
    }

    @Override
    public SortedGraphContainer process(Owner owner, Object... args)
            throws QueryArgumentClassException, UnauthorizedActionException {
        try {
            Integer[] origins = (Integer[]) args[0];
            String[] seqString = (String[]) args[1];

            if (origins.length == 0) {
                return SortedGraphContainer.emptyContainer();
            }

            SortedGraphContainer gc = new SortedGraphContainer();

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

            SequencePattern<ArrowType> sequencePattern = new SequencePattern<>();
            Map<Integer, Integer> nodeIdToDepth = new HashMap<>();
            Map<ImmutableTuple<Integer, SequencePatternElement<ArrowType>>, MatchingRecord<ArrowType>> recordMap = new HashMap<>();
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
                ArrowType unit = ArrowType.extendedValueOf(s);
                sequencePattern.append(unit, n);
            }

            for (Integer origin : origins) {
                // If any of the queried origins is not readable then it must be an illegal query request thus an
                // exception is thrown
                if (!oafService.authorized(READ, owner, crudService.getNodeFrame(origin))) {
                    throw new UnauthorizedActionException();
                }
                sequencePattern.first(firstElem -> {
                    ImmutableTuple<Integer, SequencePatternElement<ArrowType>> rKey =
                            new ImmutableTuple<>(origin, firstElem);
                    MatchingRecord<ArrowType> r = new MatchingRecord<>(origin, INIT_DEPTH, firstElem);
                    recordMap.put(rKey, r);
                    stack.push(r);
                });
            }

            PStack<MatchingRecord<ArrowType>> matchedRecords = new PStack<>();
            while (!stack.isEmpty()) {
                MatchingRecord<ArrowType> record = stack.pop();
                int nextDepth = record.depth + 1;

                // When patternElement is null, it means that the record is already at the
                // end of the sequence pattern thus shall be regarded as a successful match
                if (record.patternElement == null) {
                    matchedRecords.push(record);
                    continue;
                }

                List<FrameArrow> farrows = crudService.getAllArrowsStartingFrom(record.id, record.patternElement.unit);
                for (FrameArrow fa : farrows) {
                    if (!oafService.authorized(READ, owner, fa.getTargetFrame())) continue;
                    Integer candidate = fa.getTarget();
                    sequencePattern.next(
                            record.patternElement,
                            nextElem -> {
                                ImmutableTuple<Integer, SequencePatternElement<ArrowType>> rKey =
                                        new ImmutableTuple<>(candidate, nextElem);
                                MatchingRecord<ArrowType> r = recordMap.get(rKey);
                                if (r != null) {
                                    r.parents.add(record);
                                    if (r.depth > nextDepth) {
                                        r.depth = nextDepth;
                                    }
                                } else {
                                    r = new MatchingRecord<>(candidate, nextDepth, nextElem);
                                    r.parents.add(record);
                                    recordMap.put(rKey, r);
                                    stack.push(r);
                                }
                            }
                    );
                }
            }

            // Back-dyeing: traverse back from a matched node along the pattern
            while (!matchedRecords.isEmpty()) {
                MatchingRecord<ArrowType> r = matchedRecords.pop();
                Integer prevDepth = nodeIdToDepth.get(r.id);
                if (prevDepth == null || prevDepth > r.depth) {
                    nodeIdToDepth.put(r.id, r.depth);
                }
                for (MatchingRecord<ArrowType> parent : r.parents) {
                    matchedRecords.push(parent);
                    gc.addArrow(new Arrow(parent.id, parent.patternElement.unit, r.id));
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
