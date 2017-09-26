package photon.tube.action;

public class ActionException extends RuntimeException {
    private final Action action;

    public ActionException(Action action, Exception e) {
        super(String.format("%s %d aborted because of %s", action.getClass().getName(), action.id(), e), e);
        this.action = action;
    }

    public Action abortedAction() {
        return action;
    }
}
