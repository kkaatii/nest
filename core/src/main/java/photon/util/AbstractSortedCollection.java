package photon.util;

import java.util.*;
import java.util.stream.IntStream;

/**
 * A container class of a <tt>depth</tt> assigned to each entry inside. The key method is <tt>sort</tt>, which can
 * sort all entries by their depths thus allows pagination. <p>
 */
public abstract class AbstractSortedCollection<T> {

    public static final int INIT_DEPTH = 0;
    protected final List<T> _entries = new ArrayList<>();
    private final List<T> entries = Collections.unmodifiableList(_entries);
    protected Map<Integer, List<Integer>> depthToIndexes = new HashMap<>();
    private List<Integer> _depths = new ArrayList<>();
    protected int minDepth = Integer.MAX_VALUE;
    protected int maxDepth = Integer.MIN_VALUE;
    protected int currentDepth = INIT_DEPTH;
    protected int[] rankToIndex;
    protected int[] indexToRank;
    protected int[] rankToDepth;
    protected boolean sorted = false;
    protected int currentIndex = 0;

    public void setCurrentDepth(int depth) {
        currentDepth = depth;
    }

    public int currentDepth() {
        return currentDepth;
    }

    public int minDepth() {
        return minDepth;
    }

    public int maxDepth() {
        return maxDepth;
    }

    private void register(T entry, int depth) {
        if (depth < minDepth)
            minDepth = depth;
        if (depth > maxDepth)
            maxDepth = depth;
        depthToIndexes.computeIfAbsent(depth, d -> new ArrayList<>()).add(currentIndex++);
        _depths.add(depth);
        _entries.add(entry);
    }

    public void add(T entry) {

        sorted = false;
        register(entry, currentDepth);
    }

    public void addWithNextDepth(T entry) {
        currentDepth++;
        add(entry);
    }

    public void add(T entry, int depth) {
        currentDepth = depth;
        add(entry);
    }

    public void addAll(Collection<T> newEntries) {
        sorted = false;
        for (T newEntry : newEntries) {
            register(newEntry, currentDepth);
        }
    }

    public void addAllWithNextDepth(Collection<T> newEntries) {
        currentDepth++;
        addAll(newEntries);
    }

    public void addAll(Collection<T> newEntries, int depth) {
        currentDepth = depth;
        addAll(newEntries);
    }

    public int size() {
        return currentIndex;
    }

    public List<T> entries() {
        return entries;
    }

    public T atRank(int r) {
        if (r < 0 || r >= currentIndex)
            return null;
        return _entries.get(rankToIndex[r]);
    }

    public int depthOfRank(int r) {
        if (r < 0 || r >= currentIndex)
            throw new RuntimeException("Invalid rank!");
        return rankToDepth[r];
    }

    public AbstractSortedCollection<T> sort() {
        if (sorted)
            return this;

        if (currentIndex == 0) {
            sorted = true;
            minDepth = INIT_DEPTH;
            maxDepth = INIT_DEPTH;
            return this;
        }

        rankToDepth = _depths.stream().mapToInt(d -> d).toArray();
        rankToIndex = IntStream.range(0, currentIndex).toArray();
        indexToRank = rankToIndex.clone();

        Numeric.qsortInt2(rankToDepth, rankToIndex, 0, currentIndex - 1);
        Numeric.qsortInt2(rankToIndex.clone(), indexToRank, 0, currentIndex - 1);

        sorted = true;
        return this;
    }

    public abstract AbstractSortedCollection<T> segmentByRank(int smaller, int larger);

    public abstract AbstractSortedCollection<T> segmentByDepth(int smaller, int larger);
}
