package photon.tube.query;

import photon.tube.model.Arrow;
import photon.tube.model.Point;
import photon.util.AbstractSortedCollection;
import photon.util.Utils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

/**
 * The core data object for describing a graph. Controllers get <tt>SortedGraphContainer</tt> from <tt>QueryService</tt> and send it to
 * the client side. <p>
 * Supports the following features: <p>
 * + Containing all essential elements: <tt>Point</tt>s, <tt>Arrow</tt>s and <tt>Extension</tt>s <p>
 * + Pagination of itself by the depth of each <tt>Point</tt>
 */
public class SortedGraphContainer extends AbstractSortedCollection<Point> {

    private final Collection<Arrow> _arrows;
    private final Collection<Arrow> arrows;
    private Map<Integer, Integer> nodeIdToRank;
    private List<Arrow>[] rankToArrows;
    private Map<Integer, List<Arrow>> depthToArrows;
    private GraphInfo info;

    private static final SortedGraphContainer EMPTY_GRAPH_CONTAINER =
            fixateWith(Collections.emptyList(), Collections.emptySet());

    public SortedGraphContainer(final List<Point> points,
                                final Collection<Arrow> arrows) {
        super.addAll(points);
        this._arrows = arrows;
        this.arrows = Collections.unmodifiableCollection(_arrows);
    }

    public SortedGraphContainer() {
        this._arrows = new HashSet<>();
        this.arrows = Collections.unmodifiableCollection(_arrows);
    }

    /**
     * Be careful when using this method, as the returned <tt>SortedGraphContainer</tt> can no longer be modified.
     *
     * @param points points to be contained
     * @param arrows arrows to be contained
     * @return an <i>"immutable"</i> <tt>SortedGraphContainer</tt> as if it is just a segment of another graph
     */
    public static SortedGraphContainer fixateWith(final List<Point> points,
                                                  final Collection<Arrow> arrows) {
        return new SegmentContainer(points, arrows);
    }

    public static SortedGraphContainer emptyContainer() {
        return EMPTY_GRAPH_CONTAINER;
    }

    public List<Point> points() {
        return entries();
    }

    public Collection<Arrow> arrows() {
        return arrows;
    }

    public void addArrow(Arrow arrow) {
        sorted = false;
        _arrows.add(arrow);
    }

    public void addArrow(Collection<Arrow> arrows) {
        sorted = false;
        this._arrows.addAll(arrows);
    }

    public boolean isEmpty() {
        return size() == 0 && _arrows.isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public SortedGraphContainer sort() {
        if (sorted)
            return this;
        super.sort();
        sorted = false;

        // In case there are only points or _arrows, just pretend to be sorted.
        if (currentIndex == 0 || _arrows.isEmpty()) {
            sorted = true;
            return this;
        }

        nodeIdToRank = new HashMap<>();
        rankToArrows = (List<Arrow>[]) new ArrayList[currentIndex];
        depthToArrows = new HashMap<>();
        IntStream.range(0, currentIndex).forEach(i -> {
            nodeIdToRank.put(_entries.get(i).getId(), indexToRank[i]);
            rankToArrows[i] = new ArrayList<>();
        });
        depthToIndexes.keySet().forEach(depth -> depthToArrows.put(depth, new ArrayList<>()));

        for (Arrow a : _arrows) {
            Integer originRank = nodeIdToRank.get(a.getOrigin());
            Integer targetRank = nodeIdToRank.get(a.getTarget());
            // There is inconsistency in the ways of computing rank and depth of an arrow:
            // rank is defined as the larger of ranks of its two endpoints, while depth is
            // defined as the depth of the origin node.
            rankToArrows[Utils.maxOf(
                    originRank,
                    targetRank
            )].add(a);
            depthToArrows.get(rankToDepth[originRank]).add(a);
        }

        sorted = true;
        return this;
    }

    @Override
    public SortedGraphContainer segmentByRank(int left, int right) {
        return segmentByRank(left, right, true, true);
    }

    public SortedGraphContainer segmentByRank(int left, int right, boolean leftInclusive, boolean rightInclusive) {
        if (!sorted)
            sort();
        if (right > 0 && left > right)
            return emptyContainer();
        // In case there is no point but arrows, return all arrows if leftLimit is 0
        if (size() == 0)
            return (left == 0) ? this : emptyContainer();
        right = (right >= size()) ? size() - 1 : right;

        SortedGraphContainer result = new SortedGraphContainer();

        IntConsumer toIncludeArrows = (rankToArrows == null) ?
                r -> {
                } :
                r -> rankToArrows[r].forEach(a -> {
                    Integer originRank = nodeIdToRank.get(a.getOrigin());
                    Integer targetRank = nodeIdToRank.get(a.getTarget());
                    if (originRank >= left && targetRank >= left) {
                        result.addArrow(a);
                    }
                });

        for (int r = left; r <= right; r++) {
            if (!((r == left && !leftInclusive) || (r == right && !rightInclusive)))
                result.add(_entries.get(rankToIndex[r]), rankToDepth[r]);
            toIncludeArrows.accept(r);
        }

        result.sorted = true;

        return result;
    }

    @Override
    public SortedGraphContainer segmentByDepth(int left, int right) {
        return segmentByDepth(left, right, true, true);
    }

    public SortedGraphContainer segmentByDepth(int left, int right, boolean leftInclusive, boolean rightInclusive) {
        if (!sorted)
            sort();
        if (right > 0 && left > right)
            return emptyContainer();
        if (size() == 0)
            return (left == minDepth()) ? this : emptyContainer();

        SortedGraphContainer result = new SortedGraphContainer();
        IntConsumer toIncludeArrows = (depthToArrows == null) ?
                depth -> {
                } :
                depth -> depthToArrows.get(depth).forEach(result::addArrow);
        BiConsumer<Integer, List<Integer>> toInclude = (left == right) ?
                (depth, indexes) -> {
                    if (depth == left && leftInclusive && rightInclusive)
                        indexes.forEach(i -> result.add(_entries.get(i), depth));
                } :
                (depth, indexes) -> {
                    if (depth >= left && depth < right) {
                        if (leftInclusive || depth != left)
                            indexes.forEach(i -> result.add(_entries.get(i), depth));
                        toIncludeArrows.accept(depth);
                    } else if (depth == right && rightInclusive)
                        indexes.forEach(i -> result.add(_entries.get(i), depth));
                };

        depthToIndexes.forEach(toInclude);
        result.sorted = true;

        return result;
    }

    public SegmentGraph asSegment() {
        return new SegmentGraph(points(), arrows(), depthToIndexes);
    }

    public GraphInfo info() {
        if (!sorted || info == null) {
            if (!sorted)
                sort();
            if (info == null)
                info = new GraphInfo();
            info.minDepth = minDepth;
            info.maxDepth = maxDepth;
            info.pointCount = currentIndex;
            info.arrowCount = _arrows.size();
        }
        return info;
    }

    /**
     * Used as the result class type for <tt>segmentByRank</tt> and <tt>segmentByDepth</tt>.
     */
    private static class SegmentContainer extends SortedGraphContainer {
        SegmentContainer(final List<Point> points,
                         final Collection<Arrow> arrows) {
            super(points, arrows);
            sorted = true;
        }

        private static class ImmutableException extends UnsupportedOperationException {
            ImmutableException() {
                super("Cannot add elements to a segment container!");
            }
        }

        private static class NotSortableException extends UnsupportedOperationException {
            NotSortableException() {
                super("Cannot sort/segment a segment container!");
            }
        }

        @Override
        public SortedGraphContainer sort() {
            throw new NotSortableException();
        }

        @Override
        public SortedGraphContainer segmentByRank(int left, int right) {
            throw new NotSortableException();
        }

        @Override
        public SortedGraphContainer segmentByDepth(int left, int right) {
            throw new NotSortableException();
        }

        @Override
        public SortedGraphContainer segmentByRank(int left, int right, boolean leftInclusive, boolean rightInclusive) {
            throw new NotSortableException();
        }

        @Override
        public SortedGraphContainer segmentByDepth(int left, int right, boolean leftInclusive, boolean rightInclusive) {
            throw new NotSortableException();
        }

        @Override
        public void addArrow(Arrow arrow) {
            throw new ImmutableException();
        }

        @Override
        public void addArrow(Collection<Arrow> arrows) {
            throw new ImmutableException();
        }

        @Override
        public void setCurrentDepth(int depth) {
            throw new ImmutableException();
        }

        @Override
        public void add(Point entry) {
            throw new ImmutableException();
        }

        @Override
        public void addWithNextDepth(Point entry) {
            throw new ImmutableException();
        }

        @Override
        public void add(Point entry, int depth) {
            throw new ImmutableException();
        }

        @Override
        public void addAll(Collection<Point> newEntries) {
            throw new ImmutableException();
        }

        @Override
        public void addAllWithNextDepth(Collection<Point> newEntries) {
            throw new ImmutableException();
        }

        @Override
        public void addAll(Collection<Point> newEntries, int depth) {
            throw new ImmutableException();
        }
    }
}
