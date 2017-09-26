package photon.tube.query;

import photon.tube.action.ActionFactory;
import photon.tube.action.ActionRequest;
import photon.tube.action.Transformation;

public class Segmentation extends Transformation<GraphContainer, QueryResult> {

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
    public QueryResult transform(GraphContainer container) {
        GraphContainer graphContainer;
        if (container.isEmpty())
            graphContainer = container;
        else switch (mode) {
            case NONE:
                graphContainer = container;
                break;
            case BY_RANK:
                graphContainer = container.segmentByRank(leftLimit, rightLimit, leftInclusive, rightInclusive);
                break;
            case BY_DEPTH:
                graphContainer = container.segmentByDepth(leftLimit, rightLimit, leftInclusive, rightInclusive);
                break;
            default:
                throw new RuntimeException("Unsupported segment mode \"" + mode + "\"!");
        }
        return new QueryResult(container.info(), graphContainer.info(), graphContainer.asSegment());
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
                    try {
                        int leftLimit = actionRequest.get(Integer.class, "left_limit");
                        int rightLimit = actionRequest.get(Integer.class, "right_limit");
                        Boolean leftInclusive = actionRequest.get(Boolean.class, "left_inclusive");
                        if (leftInclusive == null) {
                            leftInclusive = Boolean.TRUE;
                        }
                        Boolean rightInclusive = actionRequest.get(Boolean.class, "right_inclusive");
                        if (rightInclusive == null) {
                            rightInclusive = Boolean.FALSE;
                        }
                        return new Segmentation(mode, leftLimit, rightLimit, leftInclusive, rightInclusive);
                    } catch (NumberFormatException nfe) {
                        throw new RuntimeException("Missing argument: left_limit or right_limit");
                    }
                default:
                    throw new RuntimeException("Unsupported segment mode \"" + mode + "\"");
            }
        }
    }
}
