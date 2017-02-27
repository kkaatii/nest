package photon.tube.action;

import photon.tube.query.SegmentGraph;
import photon.tube.query.SortedGraphContainer;

/**
 * Created by Dun Liu on 2/20/2017.
 */
public class SegmentAction extends Action<SortedGraphContainer, SegmentGraph> implements ImmediatelyActionable {

    public static final String NONE = "none";
    public static final String BY_RANK = "rank";
    public static final String BY_DEPTH = "depth";
    private final String mode;
    private final int leftLimit;
    private final int rightLimit;
    private final boolean leftInclusive;
    private final boolean rightInclusive;

    public SegmentAction(ActionManager manager,
                         String mode,
                         int leftLimit,
                         int rightLimit,
                         boolean leftInclusive,
                         boolean rightInclusive) {
        super(manager);
        this.mode = mode;
        this.leftLimit = leftLimit;
        this.rightLimit = rightLimit;
        this.leftInclusive = leftInclusive;
        this.rightInclusive = rightInclusive;
    }

    @Override
    public SegmentGraph doRun(SortedGraphContainer container) {
        if (container.isEmpty())
            return container.asSegment();
        switch (mode) {
            case NONE:
                return container.asSegment();
            case BY_RANK:
                return container.segmentByRank(leftLimit, rightLimit, leftInclusive, rightInclusive).asSegment();
            case BY_DEPTH:
                return container.segmentByDepth(leftLimit, rightLimit, leftInclusive, rightInclusive).asSegment();
            default:
                throw new RuntimeException("Unsupported segment mode \"" + mode + "\"!");
        }
    }
}
