package photon.tube.action;

import photon.tube.model.Owner;
import photon.util.GenericDict;

public class ActionRequest extends GenericDict {

    private final Owner owner;
    private String actionName;

    public ActionRequest(Owner owner) {
        this.owner = owner;
    }

    public Owner owner() {
        return owner;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String actionName() {
        return actionName;
    }

}
