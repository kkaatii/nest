package photon.tube.query.processor;

import photon.tube.auth.AuthService;
import photon.tube.auth.UnauthorizedActionException;
import photon.tube.model.*;
import photon.tube.query.GraphContainer;

import java.util.*;
import java.util.function.BiPredicate;

import static photon.tube.query.GraphContainer.INIT_DEPTH;

public class PatternProcessor extends Processor {

    private Owner owner = null;
    private Map<Integer, Integer> nodeIdToDepth = null;
    private Set<Arrow> arrowSet = null;
    private Set<Integer> testingSet = null;
    private PatternSequence patternSequence = null;

    public PatternProcessor(CrudService crudService, AuthService authService) {
        super(crudService, authService);
    }

    @Override
    public GraphContainer process(Owner owner, Object... args)
            throws QueryArgumentClassMismatchException, UnauthorizedActionException {
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
                    if (!authService.authorizedRead(owner, point.getFrame()))
                        throw new UnauthorizedActionException();
                });
                gc.addAll(points);
                return gc;
            }

            this.owner = owner;
            arrowSet = new HashSet<>();
            testingSet = new HashSet<>();
            nodeIdToDepth = new HashMap<>();
            patternSequence = new PatternSequence();
            for (int i = 0; i < seqString.length; i++) {
                if (i < seqString.length - 1 && seqString[i + 1].matches("\\d*|\\*|\\?|\\+")) {
                    patternSequence.append(seqString[i++], seqString[i]);
                } else {
                    patternSequence.append(seqString[i], "1");
                }
            }
            for (Integer origin : origins) {
                // If any of the queried origins is not readable then it must be an illegal query request and an
                // exception is thrown
                if (!authService.authorizedRead(owner, crudService.getNodeFrame(origin)))
                    throw new UnauthorizedActionException();
                testingSet.add(origin);
                if (patternSequence.first((_at, _sPos) -> testMatch(origin, _at, _sPos, INIT_DEPTH + 1)))
                    nodeIdToDepth.put(origin, INIT_DEPTH);
                testingSet.remove(origin);
            }

            Map<Integer, Point> pointMap = crudService.getPointMap(nodeIdToDepth.keySet());
            pointMap.forEach((id, point) -> gc.add(point, nodeIdToDepth.get(id)));
            gc.addArrow(arrowSet);
            return gc.sort();
        } catch (ClassCastException cce) {
            throw new QueryArgumentClassMismatchException();
        }
    }

    private boolean testMatch(Integer originId,
                              ArrowType at,
                              SequencePosition sPos,
                              int nextDepth) {
        boolean originMatched = false;
        Integer candidateDepth;
        List<FrameArrow> arrows = crudService.getAllArrowsStartingFrom(originId, at);
        for (FrameArrow a : arrows) {
            if (!authService.authorizedRead(owner, a.getTargetFrame())) continue;
            Integer candidate = a.getTarget();
            if (testingSet.contains(candidate)) continue;
            candidateDepth = nodeIdToDepth.get(candidate);
            if (candidateDepth == null) {
                testingSet.add(candidate);
                boolean matched = patternSequence.next(sPos, (_at, _sPos) ->
                        testMatch(candidate, _at, _sPos, nextDepth + 1)
                );
                if (matched) {
                    nodeIdToDepth.put(candidate, nextDepth);
                    arrowSet.add(a);
                    originMatched = true;
                }
                testingSet.remove(candidate);
            } else {
                if (candidateDepth > nextDepth)
                    nodeIdToDepth.put(candidate, nextDepth);
                arrowSet.add(a.reverse());
                originMatched = true;
            }
        }
        return originMatched;
    }

    private static class PatternSequence {
        static final int ONE_OR_MORE_TIMES = -1;
        static final int ZERO_OR_MORE_TIMES = -2;
        static final int ZERO_OR_ONE_TIME = -3;

        private final List<ArrowType> atList = new ArrayList<>();
        private final List<Integer> rawTimesList = new ArrayList<>();
        private int count = 0;

        void append(String atName, String timeString) {
            if (atName.startsWith("^"))
                atList.add(ArrowType.valueOf(atName.substring(1)).reverse());
            else
                atList.add(ArrowType.valueOf(atName));
            switch (timeString) {
                case "*":
                    rawTimesList.add(ZERO_OR_MORE_TIMES);
                    break;
                case "!":
                    rawTimesList.add(ONE_OR_MORE_TIMES);
                    break;
                case "?":
                    rawTimesList.add(ZERO_OR_ONE_TIME);
                    break;
                default:
                    rawTimesList.add(Integer.valueOf(timeString));
            }
            count++;
        }

        ArrowType getArrowType(int index) {
            return atList.get(index);
        }

        ArrowType getArrowType(SequencePosition sPos) {
            return atList.get(sPos.index);
        }

        // Returns true if the next sPos is already at the end of the sequence
        // or when the tested predicate is true
        boolean next(SequencePosition sPos, BiPredicate<ArrowType, SequencePosition> func) {
            boolean tested = false;
            if (sPos.times >= 2) {
                return func.test(getArrowType(sPos), new SequencePosition(sPos.index, sPos.times - 1));
            } else {
                if (isMany(sPos.index)) {
                    tested = func.test(getArrowType(sPos), new SequencePosition(sPos.index, 1));
                }
                int i = sPos.index + 1;
                if (i < count) {
                    tested |= func.test(getArrowType(i), new SequencePosition(i, getTimes(i)));
                    return tested || goThroughOptional(i, func);
                }
                return true;
            }
        }

        boolean first(BiPredicate<ArrowType, SequencePosition> func) {
            boolean tested = func.test(getArrowType(0), new SequencePosition(0, getTimes(0)));
            return tested || goThroughOptional(0, func);
        }

        boolean goThroughOptional(int index, BiPredicate<ArrowType, SequencePosition> func) {
            int rawTimes;
            boolean optional = isOptional(index);
            boolean tested = false;
            while (optional && ++index < count) {
                rawTimes = rawTimesList.get(index);
                tested |= func.test(getArrowType(index), new SequencePosition(index, rawTimes > 0 ? rawTimes : 1));
                optional = isOptionalRawTimes(rawTimes);
            }
            return (optional && index == count) || tested;
        }

        int getTimes(int index) {
            int rawTimes = rawTimesList.get(index);
            return rawTimes > 0 ? rawTimes : 1;
        }

        int getTimes(SequencePosition sPos) {
            return getTimes(sPos.index);
        }

        boolean isOptional(int index) {
            return isOptionalRawTimes(rawTimesList.get(index));
        }

        boolean isOptionalRawTimes(int rawTimes) {
            return (ZERO_OR_MORE_TIMES == rawTimes) || (ZERO_OR_ONE_TIME == rawTimes);
        }

        boolean isMany(int index) {
            int rawTimes = rawTimesList.get(index);
            return (ONE_OR_MORE_TIMES == rawTimes) || (ZERO_OR_MORE_TIMES == rawTimes);
        }

    }

    private static class SequencePosition {
        int index;
        int times;

        SequencePosition(int index, int times) {
            this.index = index;
            this.times = times;
        }
    }
}
