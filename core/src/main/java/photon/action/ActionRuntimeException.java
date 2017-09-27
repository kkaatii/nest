package photon.action;

public class ActionRuntimeException extends RuntimeException {
    private final Action action;

    public ActionRuntimeException(Action action, Exception e) {
        super(String.format("%s %d aborted because of %s", action.getClass().getName(), action.id(), e), e);
        this.action = action;
    }

    public ActionRuntimeException(Action action, String message) {
        super(message);
        this.action = action;
    }

    public Action abortedAction() {
        return action;
    }
}
