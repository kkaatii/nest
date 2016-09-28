package nest.util;

public class Sort {

    public static void qsortInt2(int[] n, int[] p, int left, int right) {
        int dp;
        if (left < right) {
            dp = partitionInt2(n, p, left, right);
            qsortInt2(n, p, left, dp - 1);
            qsortInt2(n, p, dp + 1, right);
        }
    }

    public static void qsortInt(int[] n, int left, int right) {
        int dp;
        int[] p = new int[0];
        if (left < right) {
            dp = partitionInt2(n, p, left, right);
            qsortInt2(n, p, left, dp - 1);
            qsortInt2(n, p, dp + 1, right);
        }
    }

    private static int partitionInt2(int[] n, int[] p, int left, int right) {
        int pivot = n[left];
        int shadow = 0;
        boolean sync = p.length > 0;
        if (sync)
            shadow = p[left];
        while (left < right) {
            while (left < right && n[right] >= pivot)
                right--;
            if (left < right) {
                if (sync)
                    p[left] = p[right];
                n[left++] = n[right];
            }
            while (left < right && n[left] <= pivot)
                left++;
            if (left < right) {
                if (sync)
                    p[right] = p[left];
                n[right--] = n[left];
            }
        }
        n[left] = pivot;
        if (sync)
            p[left] = shadow;
        return left;
    }

    public static boolean isSorted(int[] a) {
        for (int i = 0; i < a.length - 1; i++) {
            if (a[i] > a[i+1])
                return false;
        }
        return true;
    }
}
