package photon.tube.query;

import photon.tube.model.Arrow;
import photon.tube.model.Point;
import photon.util.AbstractDepthSorter;
import photon.util.Util;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

/**
 * The core data object for describing a graph. Controllers get <tt>GraphContainer</tt> from <tt>QueryService</tt> and send it to
 * the client side. <p>
 * Supports the following features: <p>
 * + Containing all essential elements: <tt>Point</tt>s, <tt>Arrow</tt>s and <tt>Extension</tt>s <p>
 * + Pagination of itself by the depth of each <tt>Point</tt>
 */
public class GraphContainer extends AbstractDepthSorter<Point> {

    private final List<Arrow> _arrows;
    private final List<Arrow> arrows;
    private Map<Integer, Integer> nodeIdToRank;
    private List<Integer>[] rankToArrowIndexes;
    private Map<Integer, List<Integer>> depthToArrowIndexes;
    private GraphInfo info;

    private static final GraphContainer EMPTY_GRAPH_CONTAINER = fixateWith(Collections.emptyList(), Collections.emptyList());

    public GraphContainer(final List<Point> points,
                          final List<Arrow> arrows) {
        super.addAll(points);
        this._arrows = arrows;
        this.arrows = Collections.unmodifiableList(_arrows);
    }

    public GraphContainer() {
        this._arrows = new ArrayList<>();
        this.arrows = Collections.unmodifiableList(_arrows);
    }

    /**
     * Be careful when using this method, as the returned <tt>GraphContainer</tt> can no longer be modified.
     *
     * @param points points to be contained
     * @param arrows arrows to be contained
     * @return an <i>"immutable"</i> <tt>GraphContainer</tt> as if it is just a section of another graph
     */
    public static GraphContainer fixateWith(final List<Point> points,
                                            final List<Arrow> arrows) {
        return new SectionContainer(points, arrows);
    }

    public static GraphContainer emptyContainer() {
        return EMPTY_GRAPH_CONTAINER;
    }

    public List<Point> points() {
        return entries();
    }

    public List<Arrow> arrows() {
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
    public GraphContainer sort() {
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
        rankToArrowIndexes = (List<Integer>[]) new ArrayList[currentIndex];
        depthToArrowIndexes = new HashMap<>();
        IntStream.range(0, currentIndex).forEach(i -> {
            nodeIdToRank.put(_entries.get(i).getId(), indexToRank[i]);
            rankToArrowIndexes[i] = new ArrayList<>();
        });
        depthToIndexes.keySet().forEach(depth -> depthToArrowIndexes.put(depth, new ArrayList<>()));

        for (int ai = 0; ai < _arrows.size(); ai++) {
            Arrow a = _arrows.get(ai);
            Integer originRank = nodeIdToRank.get(a.getOrigin());
            Integer targetRank = nodeIdToRank.get(a.getTarget());
            // There is inconsistency in the ways of computing rank and depth of an arrow:
            // rank is defined as the larger of ranks of its two endpoints, while depth is
            // defined as the depth of the origin node.
            rankToArrowIndexes[Util.maxOf(
                    originRank,
                    targetRank
            )].add(ai);
            depthToArrowIndexes.get(rankToDepth[originRank]).add(ai);
        }

        sorted = true;
        return this;
    }

    @Override
    public GraphContainer sectionByRank(int left, int right) {
        return sectionByRank(left, right, true, true);
    }

    public GraphContainer sectionByRank(int left, int right, boolean leftInclusive, boolean rightInclusive) {
        if (!sorted)
            sort();
        if (right > 0 && left > right)
            return emptyContainer();
        // In case there is no point but arrows, return all arrows if leftLimit is 0
        if (size() == 0)
            return (left == 0) ? this : emptyContainer();
        right = (right >= size()) ? size() - 1 : right;

        GraphContainer result = new GraphContainer();

        IntConsumer toIncludeArrows = (rankToArrowIndexes == null) ?
                r -> {
                } :
                r -> rankToArrowIndexes[r].forEach(ai -> {
                    Arrow a = _arrows.get(ai);
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
    public GraphContainer sectionByDepth(int left, int right) {
        return sectionByDepth(left, right, true, true);
    }

    public GraphContainer sectionByDepth(int left, int right, boolean leftInclusive, boolean rightInclusive) {
        if (!sorted)
            sort();
        if (right > 0 && left > right)
            return emptyContainer();
        if (size() == 0)
            return (left == minDepth()) ? this : emptyContainer();

        GraphContainer result = new GraphContainer();
        IntConsumer toIncludeArrows = (depthToArrowIndexes == null) ?
                depth -> {
                } :
                depth -> depthToArrowIndexes.get(depth).forEach(ai -> {
                    Arrow a = _arrows.get(ai);
                    result.addArrow(a);
                });
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

    public Section export() {
        return new Section(points(), arrows(), depthToIndexes);
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
     * Used as the result class type for <tt>sectionByRank</tt> and <tt>sectionByDepth</tt>.
     */
    private static class SectionContainer extends GraphContainer {
        SectionContainer(final List<Point> points,
                         final List<Arrow> arrows) {
            super(points, arrows);
            sorted = true;
        }

        private static class ImmutableException extends UnsupportedOperationException {
            ImmutableException() {
                super("Cannot add elements to a section container!");
            }
        }

        private static class NotSortableException extends UnsupportedOperationException {
            NotSortableException() {
                super("Cannot sort/section a section container!");
            }
        }

        @Override
        public GraphContainer sort() {
            throw new NotSortableException();
        }

        @Override
        public GraphContainer sectionByRank(int left, int right) {
            throw new NotSortableException();
        }

        @Override
        public GraphContainer sectionByDepth(int left, int right) {
            throw new NotSortableException();
        }

        @Override
        public GraphContainer sectionByRank(int left, int right, boolean leftInclusive, boolean rightInclusive) {
            throw new NotSortableException();
        }

        @Override
        public GraphContainer sectionByDepth(int left, int right, boolean leftInclusive, boolean rightInclusive) {
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
