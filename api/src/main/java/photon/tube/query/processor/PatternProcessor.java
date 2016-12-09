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
            throw new QueryArgumentClassMismatchException();
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
        private final int MAX_TIMES = Integer.MAX_VALUE - 1;

        private final List<ArrowType> atList = new ArrayList<>();
        private final List<TimesOptions> timesOptionsList = new ArrayList<>();
        private int length = 0;

        void append(String atName, String timeString) {
            ArrowType at;
            if (atName.startsWith("^"))
                at = ArrowType.valueOf(atName.substring(1)).reverse();
            else
                at = ArrowType.valueOf(atName);
            String[] options = timeString.split(",");
            for (String optionStr : options) {
                String[] segments = optionStr.split("-");
                if (segments.length > 1) {
                    int min = Integer.valueOf(segments[0]), max = Integer.valueOf(segments[1]);
                    for (int i = min; i < max; i++) {
                        timesOptionsList.add(new TimesOptions(0, 1));
                        atList.add(at);
                        length++;
                    }
                    if (min > 0) {
                        timesOptionsList.add(new TimesOptions(min));
                        atList.add(at);
                        length++;
                    }
                } else {
                    if (segments[0].endsWith("*")) {
                        int times = segments[0].equals("*")
                                ? 0
                                : Integer.valueOf(segments[0].substring(0, segments[0].length() - 1));
                        if (times > 0) {
                            timesOptionsList.add(new TimesOptions(times));
                            atList.add(at);
                            length++;
                        }
                        timesOptionsList.add(new TimesOptions(0, MAX_TIMES));
                        atList.add(at);
                        length++;
                    } else {
                        timesOptionsList.add(new TimesOptions(Integer.valueOf(segments[0])));
                        atList.add(at);
                        length++;
                    }
                }
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
            TimesOptions timesOptions;
            if (sPos.times >= 2) {
                return func.test(getArrowType(sPos), new SequencePosition(sPos.index, sPos.times - 1));
            } else {
                int i = ++sPos.index;
                if (i < length) {
                    timesOptions = timesOptionsList.get(i);
                    if (timesOptions.isDefinite()) {
                        sPos.times = timesOptions.options[0];
                        return func.test(getArrowType(i), sPos);
                    } else return Arrays.stream(timesOptions.options).anyMatch(times -> times > 0
                            ? func.test(getArrowType(i), new SequencePosition(i, times))
                            : goThroughOptional(i, func));
                }
                return true;
            }
        }

        boolean first(BiPredicate<ArrowType, SequencePosition> func) {
            TimesOptions timesOptions = timesOptionsList.get(0);
            if (timesOptions.isDefinite()) {
                return func.test(getArrowType(0), new SequencePosition(0, timesOptions.options[0]));
            } else return Arrays.stream(timesOptions.options).anyMatch(times -> times > 0
                    ? func.test(getArrowType(0), new SequencePosition(0, times))
                    : goThroughOptional(0, func));
        }

        boolean goThroughOptional(int index, BiPredicate<ArrowType, SequencePosition> func) {
            boolean optional = true, tested = false;
            TimesOptions timesOptions;
            while (optional && ++index < length) {
                timesOptions = timesOptionsList.get(index);
                if (timesOptions.isDefinite()) {
                    tested |= func.test(getArrowType(index), new SequencePosition(index, timesOptions.options[0]));
                    break;
                } else {
                    boolean anyOptional = false;
                    for (int times : timesOptions.options) {
                        if (times == 0) anyOptional = true;
                        else tested |= func.test(getArrowType(index), new SequencePosition(index, times));
                    }
                    optional = anyOptional;
                }
            }
            return tested || (optional && index == length);
        }
    }

    private static class TimesOptions {
        final int[] options;

        TimesOptions(int... options) {
            this.options = options;
        }

        boolean isDefinite() {
            return options.length == 1;
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
