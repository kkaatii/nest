package photon.tube.query;

import photon.util.Sort;

import java.util.*;
import java.util.stream.IntStream;

/**
 * A container class fixateWith a <tt>depth</tt> assigned to each entry inside. The key method is <tt>organize</tt>, which can
 * rank all _entries according to their _depths thus allowing pagination. <p>
 */
abstract class AbstractDepthSequencer<T> {

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
    protected boolean organized = false;
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
    public int maxDepth() { return maxDepth; }

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

        organized = false;
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
        organized = false;
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

    public AbstractDepthSequencer<T> organize() {
        if (organized)
            return this;

        if (currentIndex == 0) {
            organized = true;
            minDepth = INIT_DEPTH;
            maxDepth = INIT_DEPTH;
            return this;
        }

        rankToDepth = _depths.stream().mapToInt(d -> d).toArray();
        rankToIndex = IntStream.range(0, currentIndex).toArray();
        indexToRank = rankToIndex.clone();

        if (!Sort.isSorted(rankToDepth)) {
            Sort.qsortInt2(rankToDepth, rankToIndex, 0, currentIndex - 1);
            Sort.qsortInt2(rankToIndex.clone(), indexToRank, 0, currentIndex - 1);
        }

        organized = true;
        return this;
    }

    public abstract AbstractDepthSequencer<T> sectionByRank(int smaller, int larger);

    public abstract AbstractDepthSequencer<T> sectionByDepth(int smaller, int larger);
}
