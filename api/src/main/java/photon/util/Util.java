package photon.util;

import java.util.*;

public class Util {
	
	public static <T> List<T> ensureList(List<T> list) {
		return (list == null) ? Collections.emptyList() : list;
	}

	public static <T> Set<T> ensureSet(Set<T> set) {
		return (set == null) ? Collections.emptySet() : set;
	}

    public static <T extends Comparable<T>> T maxOf(T... ts) {
        int size = ts.length;
        if (size == 0)
            throw new RuntimeException("Nothing to compare!");

        T max = ts[0];
        for (int i = 1; i < size; i++) {
            if (ts[i].compareTo(max) > 0)
                max = ts[i];
        }

        return max;
    }

    public static <T extends Comparable<T>> T minOf(T... ts) {
        int size = ts.length;
        if (size == 0)
            throw new RuntimeException("Nothing to compare!");

        T min = ts[0];
        for (int i = 1; i < size; i++) {
            if (ts[i].compareTo(min) < 0)
                min = ts[i];
        }

        return min;
    }
}
