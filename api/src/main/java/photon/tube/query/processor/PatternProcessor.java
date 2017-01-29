package photon.tube.query.processor;

import photon.tube.auth.OafService;
import photon.tube.auth.UnauthorizedActionException;
import photon.tube.model.*;
import photon.tube.query.GraphContainer;
import photon.util.ImmutableTuple;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.IntStream;

import static photon.tube.auth.AccessLevel.READ;
import static photon.tube.query.GraphContainer.INIT_DEPTH;

// TODO there are other bugs related to this class, esp. when parsing a circular pattern

public class PatternProcessor extends Processor {

    private Owner owner = null;
    private Map<Integer, Integer> nodeIdToDepth = null;
    private Set<ImmutableTuple<Integer, PatternElement>> matchedSet = null;
    private Set<Arrow> arrowSet = null;
    private Set<ImmutableTuple<Integer, PatternElement>> testingSet = null;
    private Pattern pattern = null;

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

            this.owner = owner;
            arrowSet = new HashSet<>();
            testingSet = new HashSet<>();
            nodeIdToDepth = new HashMap<>();
            matchedSet = new HashSet<>();
            pattern = new Pattern();
            for (int i = 0; i < seqString.length; i++) {
                if (i + 1 == seqString.length || Character.isLetter(seqString[i + 1].charAt(0))) {
                    pattern.append(seqString[i], "1");
                } else {
                    pattern.append(seqString[i++], seqString[i]);
                }
            }
            for (Integer origin : origins) {
                // If any of the queried origins is not readable then it must be an illegal query request and an
                // exception is thrown
                if (!oafService.authorized(READ, owner, crudService.getNodeFrame(origin))) {
                    throw new UnauthorizedActionException();
                }
                pattern.first((nextAT, nextElem) -> testMatch(origin, nextAT, nextElem, INIT_DEPTH));
            }

            Map<Integer, Point> pointMap = crudService.getPointMap(nodeIdToDepth.keySet());
            pointMap.forEach((id, point) -> gc.add(point, nodeIdToDepth.get(id)));
            gc.addArrow(arrowSet);
            return gc.sort();
        } catch (ClassCastException cce) {
            throw new QueryArgumentClassException();
        }
    }

    private boolean testMatch(Integer originId, ArrowType at, PatternElement element, int depth) {
        boolean originMatched = false;
        if (matchedSet.contains(new ImmutableTuple<>(originId, element))) {
            Integer prevDepth = nodeIdToDepth.get(originId);
            if (prevDepth > depth) {
                nodeIdToDepth.put(originId, depth);
            }
            originMatched = true;
        } else {
            if (element != null) {
                if (element.isMany && !testingSet.add(new ImmutableTuple<>(originId, element))) {
                    return false;
                }
                List<FrameArrow> arrows = crudService.getAllArrowsStartingFrom(originId, at);
                for (FrameArrow a : arrows) {
                    if (!oafService.authorized(READ, owner, a.getTargetFrame())) continue;
                    Integer candidate = a.getTarget();
                    boolean matched = pattern.next(
                            element,
                            (nextAT, nextElem) -> testMatch(candidate, nextAT, nextElem, depth + 1)
                    );
                    if (matched) {
                        arrowSet.add(a);
                        originMatched = true;
                    }
                }
                if (element.isMany) {
                    testingSet.remove(new ImmutableTuple<>(originId, element));
                }
            } else {
                originMatched = true;
            }
            if (originMatched) {
                nodeIdToDepth.put(originId, depth);
                matchedSet.add(new ImmutableTuple<>(originId, element));
            }
        }
        return originMatched;
    }

    private static class Pattern {
        private final int MANY_TIMES = -1;

        private final List<ArrowType> atList = new ArrayList<>();
        private final List<int[]> timesOptionsList = new ArrayList<>();
        private int length = 0;

        void append(String atName, String timeString) {
            ArrowType at;
            if (atName.startsWith("^")) {
                at = ArrowType.valueOf(atName.substring(1)).reverse();
            } else {
                at = ArrowType.valueOf(atName);
            }
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
                        timesOptionsList.add(new int[]{MANY_TIMES, 0});
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

        ArrowType getArrowType(PatternElement element) {
            return atList.get(element.index);
        }

        // Returns true if the next element is already at the end of the sequence
        // or when the tested predicate is true
        boolean next(PatternElement element, BiPredicate<ArrowType, PatternElement> func) {
            int[] timesOptions;
            boolean tested = false;
            int i = element.index;
            if (element.times >= 2) {
                return func.test(getArrowType(element), new PatternElement(i, element.times - 1, false));
            } else {
                i++;
                if (i < length) {
                    timesOptions = timesOptionsList.get(i);
                    int times, length = timesOptions.length;
                    // Start with the largest number of times
                    for (int j = length; j > 0; j--) {
                        times = timesOptions[j - 1];
                        if (times == 0) {
                            tested |= goThroughOptional(i, func);
                        } else if (times > 0) {
                            tested |= func.test(getArrowType(i), new PatternElement(i, times, false));
                        } else if (times < 0) {
                            tested |= func.test(getArrowType(i), new PatternElement(i, 1, true));
                        }
                    }
                } else {
                    tested = func.test(null, null);
                }
                if (element.isMany) {
                    tested |= func.test(getArrowType(element), new PatternElement(i - 1, 1, true));
                }
                return tested;
            }
        }

        boolean first(BiPredicate<ArrowType, PatternElement> func) {
            return next(new PatternElement(-1, 0, false), func);
        }

        boolean goThroughOptional(int index, BiPredicate<ArrowType, PatternElement> func) {
            boolean optional = true, tested = false;
            int[] timesOptions;
            while (optional && ++index < length) {
                timesOptions = timesOptionsList.get(index);
                boolean anyOptional = false;
                int times, length = timesOptions.length;
                for (int j = length; j > 0; j--) {
                    times = timesOptions[j - 1];
                    if (times == 0) {
                        anyOptional = true;
                    } else if (times > 0) {
                        tested |= func.test(getArrowType(index), new PatternElement(index, times, false));
                    } else if (times < 0) {
                        tested |= func.test(getArrowType(index), new PatternElement(index, 1, true));
                    }
                }
                optional = anyOptional;
            }
            return tested || (optional && index == length);
        }
    }

    private static class PatternElement {
        int index;
        int times;
        boolean isMany;

        PatternElement(int index, int times, boolean isMany) {
            this.index = index;
            this.times = times;
            this.isMany = isMany;
        }

        @Override
        public int hashCode() {
            return index;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof PatternElement && index == ((PatternElement) o).index;
        }
    }
}
