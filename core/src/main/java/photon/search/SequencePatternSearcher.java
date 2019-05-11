package photon.search;

import photon.crud.OafService;
import photon.crud.UnauthorizedException;
import photon.model.*;
import photon.query.FailedQueryException;
import photon.query.GraphContainer;
import photon.crud.CrudService;
import photon.util.ImmutableTuple;
import photon.util.PStack;
import photon.util.GenericDict;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static photon.crud.AccessLevel.READ;
import static photon.util.AbstractSortedCollection.INIT_DEPTH;

/**
 * Searches for graph that matches a <code>SequencePattern</code>. A <code>SequencePattern</code> can be parsed
 * from a <code>String</code> array. Examples: <br>
 * <code>["parent_of", 1]</code><br>
 * <code>["parent_of", "dependent_on", 2]</code><br>
 * <code>["parent_of", "1|4-5", "^dependent_on"]</code><br>
 * <code>["parent_of", "3*", "dependent_on", "*"]</code><br><br>
 * NOTE: unlike other <code>Searcher</code>s, <code>ArrowType.WILDCARD</code> is not supported due to performance concern.
 */
class SequencePatternSearcher extends Searcher {

    private int MAX_QUERY_DEPTH = 255;

    public SequencePatternSearcher(CrudService crudService, OafService oafService) {
        super(crudService, oafService);
    }

    @Override
    public GraphContainer search(Owner owner, GenericDict params)
            throws UnauthorizedException {
        int[] origins = params.get("origins", int[].class);
        String[] seqStrings = params.get("sequence", String[].class);
        if (origins.length == 0) {
            return GraphContainer.emptyContainer();
        }

        GraphContainer gc = new GraphContainer();

        if (seqStrings.length == 0) {
            List<Point> points = crudService.listPoints(Arrays.stream(origins).boxed().collect(Collectors.toList()));
            points.forEach(point -> {
                if (!oafService.authorized(READ, owner, point.getFrame())) {
                    throw new UnauthorizedException();
                }
            });
            gc.addAll(points);
            return gc;
        }

        SequencePattern<ArrowType> sequencePattern = parseSeq(seqStrings);

        Map<Integer, Integer> nodeIdToDepth = new HashMap<>();
        Map<ImmutableTuple<Integer, SequencePatternElement<ArrowType>>, MatchingRecord<ArrowType>> recordMap = new HashMap<>();
        PStack<MatchingRecord<ArrowType>> stack = new PStack<>();

        for (Integer origin : origins) {
            // If any of the queried origins is not readable then it must be an illegal query request thus an
            // exception is thrown
            if (!oafService.authorized(READ, owner, crudService.getNodeFrame(origin))) {
                throw new UnauthorizedException();
            }
            sequencePattern.first(firstElem -> {
                ImmutableTuple<Integer, SequencePatternElement<ArrowType>> rKey =
                        new ImmutableTuple<>(origin, firstElem);
                MatchingRecord<ArrowType> r = new MatchingRecord<>(origin, INIT_DEPTH, firstElem);
                recordMap.put(rKey, r);
                stack.push(r);
            });
        }

        // DFS: prioritize finding one fully matched patch
        PStack<MatchingRecord<ArrowType>> matchedRecords = new PStack<>();
        while (!stack.isEmpty()) {
            MatchingRecord<ArrowType> record = stack.pop();
            int nextDepth = record.depth + 1;

            // When patternElement is null, it means that the record is already at the
            // end of the sequence pattern thus is indeed a successful match
            if (record.patternElement == null) {
                matchedRecords.push(record);
                continue;
            }

            List<FrameArrow> farrows = crudService.listFrameArrowsStartingFrom(record.id, record.patternElement.unit);
            // farrows contains all arrows matched with the current record
            for (FrameArrow fa : farrows) {
                if (!oafService.authorized(READ, owner, fa.getTargetFrame())) continue;
                Integer candidate = fa.getTarget();
                // push the next record-to-be-matched into stack
                sequencePattern.next(
                        record.patternElement,
                        nextElem -> {
                            ImmutableTuple<Integer, SequencePatternElement<ArrowType>> rKey =
                                    new ImmutableTuple<>(candidate, nextElem);
                            MatchingRecord<ArrowType> r = recordMap.get(rKey);

                            // Check if we've come to the same position (represented by a record) again:
                            // 'same' is defined as having identical node, identical index (in the sequence pattern),
                            // identical ArrowType, as well as identical times-to-be-matched (be it definite or indefinite).
                            // If yes, update the parent and depth info, otherwise put a new record into recordMap
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

        Map<Integer, Point> pointMap = crudService.pointMapOf(nodeIdToDepth.keySet());
        pointMap.forEach((id, point) -> gc.add(point, nodeIdToDepth.get(id)));
        return gc;
    }

    private static SequencePattern<ArrowType> parseSeq(String[] seqStrings) {
        SequencePattern<ArrowType> sequencePattern = new SequencePattern<>();
        for (int i = 0; i < seqStrings.length; i++) {
            String s, n;
            if (i + 1 == seqStrings.length || Character.isLetter(seqStrings[i + 1].charAt(0))) {
                s = seqStrings[i];
                n = "1";
            } else {
                s = seqStrings[i++];
                n = seqStrings[i];
            }
            ArrowType unit = ArrowType.extendedValueOf(s);
            if (unit.equals(ArrowType.WILDCARD)) {
                throw new FailedQueryException("Sequence Pattern Search does not support wildcard ArrowType");
            }
            sequencePattern.append(unit, n);
        }
        return sequencePattern;
    }
}
