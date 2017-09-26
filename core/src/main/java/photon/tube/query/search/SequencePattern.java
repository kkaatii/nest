package photon.tube.query.search;

import java.util.*;
import java.util.function.Consumer;

/**
 * Stores a series of elements of type <code>T</code> as well as the times to be matched of each element.
 * @param <T> any type to be stored
 */
public class SequencePattern<T> {

    private final List<T> unitList;
    private final List<int[]> timesOptionsList;
    private int length = 0;

    public SequencePattern() {
        unitList = new ArrayList<>();
        timesOptionsList = new ArrayList<>();
    }

    public SequencePattern(List<T> unitList, List<int[]> timesOptionsList) {
        this.unitList = unitList;
        this.timesOptionsList = timesOptionsList;
    }

    public void append(T unit, String timeString) {
        String[] options = timeString.split("\\|");

        Set<Integer> lst = new HashSet<>();
        for (String option : options) {
            String[] segments = option.split("-");
            if (segments.length > 1) {
                int min = Integer.parseInt(segments[0]), max = Integer.parseInt(segments[1]);
                for (int j = min; j <= max; j++) {
                    lst.add(j);
                }
            } else {
                if (option.endsWith("*")) {
                    int times = option.equals("*") ? 0 : Integer.parseInt(option.substring(0, option.length() - 1));
                    if (times == 0) {
                        lst.add(-1);
                    }
                    lst.add(-times);

                } else {
                    lst.add(Integer.parseInt(option));
                }
            }
        }
        timesOptionsList.add(lst.stream().mapToInt(i -> i).toArray());
        unitList.add(unit);
        length++;
    }

    public void next(SequencePatternElement<T> element, Consumer<SequencePatternElement<T>> func) {
        int[] timesOptions;
        int index = element.index;
        if (element.times >= 2) {
            func.accept(new SequencePatternElement<>(index, unitList.get(element.index), element.times - 1, element.isIndefinite));
        } else {
            index++;
            if (index < length) {
                timesOptions = timesOptionsList.get(index);
                T unit = unitList.get(index);
                int times, length = timesOptions.length;
                // Start with the largest number of times
                for (int j = length; j > 0; j--) {
                    times = timesOptions[j - 1];
                    if (times == 0) {
                        traverseZeroes(index, func);
                    } else if (times > 0) {
                        func.accept(new SequencePatternElement<>(index, unit, times, false));
                    } else { // The number of times is indefinite
                        func.accept(new SequencePatternElement<>(index, unit, -times, true));
                    }
                }
            } else {
                func.accept(null);
            }
            if (element.isIndefinite) {
                func.accept(new SequencePatternElement<>(index - 1, element.unit, 1, true));
            }
        }
    }

    public void first(Consumer<SequencePatternElement<T>> func) {
        next(new SequencePatternElement<>(-1, null, 0, false), func);
    }

    private void traverseZeroes(int index, Consumer<SequencePatternElement<T>> func) {
        boolean isZero = true;
        while (isZero && ++index < length) {
            int[] timesOptions = timesOptionsList.get(index);
            T unit = unitList.get(index);
            boolean anyZero = false;
            int length = timesOptions.length;
            for (int j = length; j > 0; j--) {
                int times = timesOptions[j - 1];
                if (times == 0) {
                    anyZero = true;
                } else if (times > 0) {
                    func.accept(new SequencePatternElement<>(index, unit, times, false));
                } else { // The number of times is indefinite
                    func.accept(new SequencePatternElement<>(index, unit, -times, true));
                }
            }
            isZero = anyZero;
        }
        if (index == length) {
            func.accept(null);
        }
    }
}
