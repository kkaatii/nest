package photon.tube.action;

public class ActionRuntimeException extends RuntimeException {
    private final Action action;

    public ActionRuntimeException(Action action, Exception e) {
        super(e);
        this.action = action;
    }
}
