package photon.util;

import org.jsoup.Jsoup;

import java.util.*;

public final class Utils {

    public static final int FLAG_NAN = 0;
    public static final int FLAG_BOOL= 1;
    public static final int FLAG_INT = 2;
    public static final int FLAG_FLOAT = 3;

    public static <T> List<T> ensureList(List<T> list) {
        return (list == null) ? Collections.emptyList() : list;
    }

    public static <T> Set<T> ensureSet(Set<T> set) {
        return (set == null) ? Collections.emptySet() : set;
    }

    @SafeVarargs
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

    @SafeVarargs
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

    /**
     * Test if a string is parsable.
     * Examples:
     * <p>
     * <code>1</code>, <code>-1</code>, <code>+1</code> - parsable int
     * <p>
     * <code>1.0</code>, <code>-1.</code> - parsable float
     * <p>
     * <code>True</code>, <code>false</code> - parsable boolean
     * <p>
     * <code>1x</code>, <code>+</code>, <code>-</code>, <code>.5</code>, <code>2.3.1</code> - unparsable
     *
     * @param str the string to check
     * @return <code>FLAG_INT</code> if the string is an integer; <code>FLAG_FLOAT</code> if the string is a real number;
     * <code>FLAG_NAN</code> if the string is not parsable
     */
    public static int isParsable(String str) {
        if (str == null) {
            return FLAG_NAN;
        }
        int length = str.length();
        if (length == 0) {
            return FLAG_NAN;
        }
        if (str.equals("True") || str.equals("False") || str.equals("true") || str.equals("false")) {
            return FLAG_BOOL;
        }
        int i = 0;
        if (str.charAt(0) == '-' || str.charAt(0) == '+') {
            if (length == 1) {
                return FLAG_NAN;
            }
            i = 1;
        }
        int flag = FLAG_INT;
        for (int j = i; j < length; j++) {
            char c = str.charAt(j);
            if (c <= '/' || c >= ':') {
                if (c == '.' && j > i && flag != FLAG_FLOAT) {
                    flag = FLAG_FLOAT;
                } else {
                    return FLAG_NAN;
                }
            }
        }
        return flag;
    }

    public static boolean hasFlag(int s, int flag) {
        return (s & flag) == flag;
    }
}
