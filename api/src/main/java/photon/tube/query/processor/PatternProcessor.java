package photon.tube.query.processor;

import photon.tube.auth.AuthService;
import photon.tube.auth.UnauthorizedActionException;
import photon.tube.model.*;
import photon.tube.query.GraphContainer;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.IntStream;

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
            for (int i = 0; i < seqString.length - 1; i++) {
                if (i + 1 == seqString.length || Character.isLetter(seqString[i + 1].charAt(0))) {
                    patternSequence.append(seqString[i], "1");
                } else {
                    patternSequence.append(seqString[i++], seqString[i]);
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
            throw new QueryArgumentClassException();
        }
    }

    private boolean testMatch(Integer originId, ArrowType at, SequencePosition sPos, int nextDepth) {
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
        private final int MAX_TIMES = -1;

        private final List<ArrowType> atList = new ArrayList<>();
        private final List<int[]> timesOptionsList = new ArrayList<>();
        private int length = 0;

        void append(String atName, String timeString) {
            ArrowType at;
            if (atName.startsWith("^"))
                at = ArrowType.valueOf(atName.substring(1)).reverse();
            else
                at = ArrowType.valueOf(atName);
            String[] options = timeString.split("\\|");
            if (options.length == 1) {
                String optionStr = options[0];
                String[] segments = optionStr.split("-");
                if (segments.length > 1) {
                    int min = Integer.parseInt(segments[0]), max = Integer.parseInt(segments[1]);
                    timesOptionsList.add(IntStream.rangeClosed(min, max).toArray());
                    atList.add(at);
                    length++;
                } else {
                    if (segments[0].endsWith("*")) {
                        int times = segments[0].equals("*")
                                ? 0
                                : Integer.parseInt(segments[0].substring(0, segments[0].length() - 1));
                        if (times > 0) {
                            timesOptionsList.add(new int[]{times});
                            atList.add(at);
                            length++;
                        }
                        timesOptionsList.add(new int[]{0, MAX_TIMES});
                        atList.add(at);
                        length++;
                    } else {
                        timesOptionsList.add(new int[]{Integer.parseInt(segments[0])});
                        atList.add(at);
                        length++;
                    }
                }
            } else {
                timesOptionsList.add(Arrays.stream(options).mapToInt(Integer::parseInt).toArray());
                atList.add(at);
                length++;
            }

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
            int[] timesOptions;
            boolean tested = false;
            if (--sPos.times >= 1) {
                return func.test(getArrowType(sPos), sPos);
            } else {
                if (sPos.isMany) {
                    tested = func.test(getArrowType(sPos), new SequencePosition(sPos.index, 1, true));
                }
                int i = ++sPos.index;
                if (i < length) {
                    timesOptions = timesOptionsList.get(i);
                    for (int times : timesOptions) {
                        if (times == 0) tested |= goThroughOptional(i, func);
                        else if (times > 0)
                            tested |= func.test(getArrowType(i), new SequencePosition(i, times, false));
                        else tested |= func.test(getArrowType(i), new SequencePosition(i, 1, true));
                    }
                    return tested;
                }
                return true;
            }
        }

        boolean first(BiPredicate<ArrowType, SequencePosition> func) {
            return next(new SequencePosition(-1, 0, false), func);
        }

        boolean goThroughOptional(int index, BiPredicate<ArrowType, SequencePosition> func) {
            boolean optional = true, tested = false;
            int[] timesOptions;
            while (optional && ++index < length) {
                timesOptions = timesOptionsList.get(index);
                boolean anyOptional = false;
                for (int times : timesOptions) {
                    if (times == 0) anyOptional = true;
                    else if (times > 0)
                        tested |= func.test(getArrowType(index), new SequencePosition(index, times, false));
                    else tested |= func.test(getArrowType(index), new SequencePosition(index, 1, true));
                }
                optional = anyOptional;
            }
            return tested || (optional && index == length);
        }
    }

    private static class SequencePosition {
        int index;
        int times;
        boolean isMany;

        SequencePosition(int index, int times, boolean isMany) {
            this.index = index;
            this.times = times;
            this.isMany = isMany;
        }
    }
}
