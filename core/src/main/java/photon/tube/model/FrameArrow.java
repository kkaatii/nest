package photon.tube.model;

/**
 * Arrow with target frame information. Serves as the standard arrow query result so authorization during query
 * costs fewer DB accesses.
 */
public class FrameArrow extends Arrow {
    private String targetFrame;

    public FrameArrow() {
        super();
    }

    public FrameArrow(Integer origin, ArrowType type, Integer target) {
        super(origin, type, target);
        this.targetFrame = "";
    }

    public FrameArrow(Arrow a) {
        this.origin = a.origin;
        this.type = a.type;
        this.target = a.target;
        this.active = a.active;
        this.targetFrame = "";
    }

    @Override
    public FrameArrow reverse() {
        return new FrameArrow(super.reverse());
    }

    public String getTargetFrame() {
        return targetFrame;
    }

    public void setTargetFrame(String targetFrame) {
        this.targetFrame = targetFrame;
    }
}
