package photon.tube.query.pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class Pattern<T> {
    private final int INFINITE_TIMES = -1;

    private final List<T> unitList;
    private final List<int[]> timesOptionsList;
    private int length = 0;

    public Pattern() {
        unitList = new ArrayList<>();
        timesOptionsList = new ArrayList<>();
    }

    public Pattern(List<T> unitList, List<int[]> timesOptionsList) {
        this.unitList = unitList;
        this.timesOptionsList = timesOptionsList;
    }

    public void append(T unit, String timeString) {
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
                    timesOptionsList.add(new int[]{INFINITE_TIMES, 0});
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

    public void next(PatternSegment<T> element, Consumer<PatternSegment<T>> func) {
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
                    } else if (times == INFINITE_TIMES) {
                        func.accept(new PatternSegment<>(i, 1, true, getUnit(i)));
                    }
                }
            } else {
                func.accept(null);
            }
            if (element.isInfinite) {
                func.accept(new PatternSegment<>(i - 1, 1, true, element.unit));
            }
        }
    }

    public void first(Consumer<PatternSegment<T>> func) {
        next(new PatternSegment<>(-1, 0, false, null), func);
    }

    private void goThroughOptional(int index, Consumer<PatternSegment<T>> func) {
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
                } else if (times == INFINITE_TIMES) {
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
