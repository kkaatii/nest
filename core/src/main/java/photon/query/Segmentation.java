package photon.query;

import photon.action.ActionCreationException;
import photon.action.ActionFactory;
import photon.action.ActionRequest;
import photon.action.Transformation;

public class Segmentation extends Transformation<GraphContainer, SearchResult> {

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
    public SearchResult transform(GraphContainer container) {
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
        return new SearchResult(container.info(), graphContainer.info(), graphContainer.asSegment());
    }

    public static class Factory extends ActionFactory<Segmentation> {
        private static final String actionName = "segment";

        public Factory() {
            super(actionName);
        }

        @Override
        public Segmentation createAction(ActionRequest actionRequest) {
            String mode = actionRequest.get("mode", String.class);
            switch (mode) {
                case NONE:
                    return none();
                case BY_DEPTH:
                case BY_RANK:
                    try {
                        int leftLimit = actionRequest.get("left_limit", Integer.class);
                        int rightLimit = actionRequest.get("right_limit", Integer.class);
                        Boolean leftInclusive = actionRequest.get("left_inclusive", Boolean.class);
                        if (leftInclusive == null) {
                            leftInclusive = Boolean.TRUE;
                        }
                        Boolean rightInclusive = actionRequest.get("right_inclusive", Boolean.class);
                        if (rightInclusive == null) {
                            rightInclusive = Boolean.FALSE;
                        }
                        return new Segmentation(mode, leftLimit, rightLimit, leftInclusive, rightInclusive);
                    } catch (NullPointerException | NumberFormatException e) {
                        throw new ActionCreationException("Missing argument for segmentation: left_limit or right_limit");
                    }
                default:
                    throw new ActionCreationException("Unsupported segmentation mode \"" + mode + "\"");
            }
        }
    }
}
