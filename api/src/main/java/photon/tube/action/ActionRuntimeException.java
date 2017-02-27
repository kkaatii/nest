package photon.tube.action;

public class ActionRuntimeException extends RuntimeException {
    private final Actionable action;

    public ActionRuntimeException(Actionable action, Exception e) {
        super(e);
        this.action = action;
    }
}
