package photon.tube.action;

import photon.tube.graph.SortedGraphContainer;
import photon.tube.query.QueryResult;

/**
 * Created by Dun Liu on 2/20/2017.
 */
public class Segmentation extends Transformation<SortedGraphContainer, QueryResult> {

    public static final Factory FACTORY = new Factory();
    public static final String BY_RANK = "rank";
    public static final String NONE = "none";
    public static final String BY_DEPTH = "depth";

    private final String mode;
    private final int leftLimit;
    private final int rightLimit;
    private final boolean leftInclusive;
    private final boolean rightInclusive;

    public Segmentation(String mode,
                        int leftLimit,
                        int rightLimit,
                        boolean leftInclusive,
                        boolean rightInclusive) {
        this.mode = mode;
        this.leftLimit = leftLimit;
        this.rightLimit = rightLimit;
        this.leftInclusive = leftInclusive;
        this.rightInclusive = rightInclusive;
    }

    public static Segmentation none() {
        return new Segmentation(NONE, 0, 0, false, false);
    }

    @Override
    public QueryResult transform(SortedGraphContainer container) {
        SortedGraphContainer sortedGraphContainer;
        if (container.isEmpty())
            sortedGraphContainer = container;
        else switch (mode) {
            case NONE:
                sortedGraphContainer = container;
                break;
            case BY_RANK:
                sortedGraphContainer = container.segmentByRank(leftLimit, rightLimit, leftInclusive, rightInclusive);
                break;
            case BY_DEPTH:
                sortedGraphContainer = container.segmentByDepth(leftLimit, rightLimit, leftInclusive, rightInclusive);
                break;
            default:
                throw new RuntimeException("Unsupported segment mode \"" + mode + "\"!");
        }
        return new QueryResult(container.info(), sortedGraphContainer.info(), sortedGraphContainer.asSegment());
    }

    @Override
    public final boolean isImmediate() {
        return true;
    }

    public static class Factory extends ActionFactory<Segmentation> {
        private static final String actionName = "segment";

        public Factory() {
            super(actionName);
        }

        @Override
        public Segmentation createAction(ActionRequest actionRequest) {
            String mode = actionRequest.get(String.class, "mode");
            switch (mode) {
                case NONE:
                    return none();
                case BY_DEPTH:
                case BY_RANK:
                    int leftLimit = actionRequest.get(Integer.class, "left_limit");
                    int rightLimit = actionRequest.get(Integer.class, "right_limit");
                    boolean leftInclusive = actionRequest.get(Boolean.class, "left_inclusive");
                    boolean rightInclusive = actionRequest.get(Boolean.class, "right_inclusive");
                    return new Segmentation(mode, leftLimit, rightLimit, leftInclusive, rightInclusive);
                default:
                    throw new RuntimeException("Unsupported segment mode \"" + mode + "\"!");
            }
        }
    }
}
