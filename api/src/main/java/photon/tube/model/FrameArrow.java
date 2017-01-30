package photon.tube.model;

/**
 * Created by Dun Liu on 11/11/2016.
 */
public class FrameArrow extends Arrow {
    private String targetFrame;

    public FrameArrow() {
        super();
    }

    public FrameArrow(Integer origin, ArrowType type, Integer target) {
        super(origin, type, target);
        this.targetFrame = null;
    }

    public FrameArrow(Arrow a) {
        this.origin = a.origin;
        this.type = a.type;
        this.target = a.target;
        this.active = a.active;
        this.targetFrame = null;
    }

    public String getTargetFrame() {
        return targetFrame;
    }

    public void setTargetFrame(String targetFrame) {
        this.targetFrame = targetFrame;
    }
}
