package photon.tube.action;

/**
 * Created by Dun Liu on 2/22/2017.
 */
public abstract class ActionFactory<ActionType extends Action> {
    private final String actionName;
    public ActionFactory(String actionName) {
        this.actionName = actionName;
    }

    public String actionName() {
        return actionName;
    }

    public abstract ActionType createAction(ActionRequest actionRequest);
}
