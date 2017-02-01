package photon.util;

import org.jsoup.Jsoup;

import java.util.*;

public final class Utils {

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

    public static String html2text(String html) {
        return Jsoup.parse(html).text();
    }

    public static String obj2json(Object... args) {
        int length = args.length;
        if (length % 2 != 0)
            throw new RuntimeException("Field name and value failed to pair");
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < length; i += 2) {
            sb.append("\"");
            sb.append(args[i]);
            sb.append("\":");
            Object o = args[i + 1];
            if (o instanceof String) {
                sb.append("\"");
                sb.append(o);
                sb.append("\"");
            } else {
                sb.append(o);
            }
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}");
        return sb.toString();
    }
}
