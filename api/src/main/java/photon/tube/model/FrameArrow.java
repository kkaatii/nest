package photon.tube.model;

/**
 * Created by Dun Liu on 11/11/2016.
 */
public class FrameArrow extends Arrow {
    private String targetFrame;

    public FrameArrow() {
        super();
    }

    public String getTargetFrame() {
        return targetFrame;
    }

    public void setTargetFrame(String targetFrame) {
        this.targetFrame = targetFrame;
    }
}
