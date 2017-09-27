package photon.action;

/**
 * Each type of <i>predefined</i> <code>Action</code> has its own <code>ActionFactory</code>.
 * Only when the factory and the request have identical <code>actionName</code> is the behavior of
 * <code>createAction()</code> predictable and meaningful.
 */
public abstract class ActionFactory<ActionType extends Action> {
    private final String actionName;
    public ActionFactory(String actionName) {
        this.actionName = actionName;
    }

    public String actionName() {
        return actionName;
    }

    /**
     * Accepts an <code>ActionRequest</code> and returns a <i>predefined</i> <code>Action</code> configured by the request.
     *
     * @param actionRequest an extension of <code>GenericDict</code> holding request arguments
     * @return the desired <code>Action</code>
     */
    public abstract ActionType createAction(ActionRequest actionRequest);
}
