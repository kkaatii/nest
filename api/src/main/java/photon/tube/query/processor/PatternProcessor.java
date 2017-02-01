package photon.tube.query.processor;

import photon.tube.auth.OafService;
import photon.tube.auth.UnauthorizedActionException;
import photon.tube.model.*;
import photon.tube.query.GraphContainer;
import photon.util.ImmutableTuple;
import photon.util.PStack;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;

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

    private static class Pattern<T> {
        private final int MANY_TIMES = -1;

        private final List<T> unitList = new ArrayList<>();
        private final List<int[]> timesOptionsList = new ArrayList<>();
        private int length = 0;

        void append(T unit, String timeString) {
            String[] options = timeString.split("\\|");
            if (options.length == 1) {
                String optionStr = options[0];
                String[] segments = optionStr.split("-");
                if (segments.length > 1) {
                    int min = Integer.parseInt(segments[0]), max = Integer.parseInt(segments[1]);
                    timesOptionsList.add(IntStream.rangeClosed(min, max).toArray());
                    unitList.add(unit);
                    length++;
                } else {
                    if (segments[0].endsWith("*")) {
                        int times = segments[0].equals("*")
                                ? 0
                                : Integer.parseInt(segments[0].substring(0, segments[0].length() - 1));
                        if (times > 0) {
                            timesOptionsList.add(new int[]{times});
                            unitList.add(unit);
                            length++;
                        }
                        timesOptionsList.add(new int[]{MANY_TIMES, 0});
                        unitList.add(unit);
                        length++;
                    } else {
                        timesOptionsList.add(new int[]{Integer.parseInt(segments[0])});
                        unitList.add(unit);
                        length++;
                    }
                }
            } else {
                timesOptionsList.add(Arrays.stream(options).mapToInt(Integer::parseInt).toArray());
                unitList.add(unit);
                length++;
            }

        }

        T getUnit(int index) {
            return unitList.get(index);
        }

        T getUnit(PatternSegment element) {
            return unitList.get(element.index);
        }

        void next(PatternSegment<T> element, Consumer<PatternSegment<T>> func) {
            int[] timesOptions;
            int i = element.index;
            if (element.times >= 2) {
                func.accept(new PatternSegment<>(i, element.times - 1, false, getUnit(element)));
            } else {
                i++;
                if (i < length) {
                    timesOptions = timesOptionsList.get(i);
                    int times, length = timesOptions.length;
                    // Start with the largest number of times
                    for (int j = length; j > 0; j--) {
                        times = timesOptions[j - 1];
                        if (times == 0) {
                            goThroughOptional(i, func);
                        } else if (times > 0) {
                            func.accept(new PatternSegment<>(i, times, false, getUnit(i)));
                        } else if (times == MANY_TIMES) {
                            func.accept(new PatternSegment<>(i, 1, true, getUnit(i)));
                        }
                    }
                } else {
                    func.accept(null);
                }
                if (element.isMany) {
                    func.accept(new PatternSegment<>(i - 1, 1, true, element.unit));
                }
            }
        }

        void first(Consumer<PatternSegment<T>> func) {
            next(new PatternSegment<>(-1, 0, false, null), func);
        }

        void goThroughOptional(int index, Consumer<PatternSegment<T>> func) {
            boolean optional = true;
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
                        func.accept(new PatternSegment<>(index, times, false, getUnit(index)));
                    } else if (times == MANY_TIMES) {
                        func.accept(new PatternSegment<>(index, 1, true, getUnit(index)));
                    }
                }
                optional = anyOptional;
            }
            if (index == length) {
                func.accept(null);
            }
        }
    }

    private static class PatternSegment<T> {
        int index;
        int times;
        boolean isMany;
        T unit;

        PatternSegment(int index, int times, boolean isMany, T unit) {
            this.index = index;
            this.times = times;
            this.isMany = isMany;
            this.unit = unit;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PatternSegment<?> that = (PatternSegment<?>) o;

            return index == that.index && times == that.times && isMany == that.isMany && unit.equals(that.unit);
        }

        @Override
        public int hashCode() {
            int result = index;
            result = 31 * result + times;
            result = 31 * result + (isMany ? 1 : 0);
            result = 31 * result + unit.hashCode();
            return result;
        }
    }

    private class MatchingRecord<T> {
        int id;
        int depth;
        PatternSegment<T> segment;
        List<MatchingRecord<T>> parents;

        MatchingRecord(int id, int depth, PatternSegment<T> segment) {
            this.id = id;
            this.depth = depth;
            this.segment = segment;
            this.parents = new ArrayList<>();
        }
    }
}
