package photon.tube.query;

public class SegmentSpec {
    public static final String NONE = "none";
    public static final String BY_RANK = "rank";
    public static final String BY_DEPTH = "depth";

    public String mode;
    public int leftLimit;
    public int rightLimit;
    public boolean leftInclusive;
    public boolean rightInclusive;

    public SegmentSpec(String mode, int leftLimit, int rightLimit, boolean leftInclusive, boolean rightInclusive) {
        this.mode = mode;
        this.leftLimit = leftLimit;
        this.rightLimit = rightLimit;
        this.leftInclusive = leftInclusive;
        this.rightInclusive = rightInclusive;
    }

    public SortedGraphContainer applyTo(SortedGraphContainer container) {
        if (container.isEmpty())
            return container;
        switch (mode) {
            case NONE:
                return container;
            case BY_RANK:
                return container.segmentByRank(leftLimit, rightLimit, leftInclusive, rightInclusive);
            case BY_DEPTH:
                return container.segmentByDepth(leftLimit, rightLimit, leftInclusive, rightInclusive);
            default:
                throw new RuntimeException("Unsupported segment mode \"" + mode + "\"!");
        }
    }
}