package photon.tube.action;

/**
 * Created by dan on 25/09/2017.
 */

// TODO finish ActionFlow
public class ActionFlow extends Action {

    private ActionManager manager = ActionManager.getInstance();

    public ActionFlow() {
        setAlwaysRerun(true);
    }

    public void setManager(ActionManager manager) {
        this.manager = manager;
    }

    public ActionManager getManager() {
        return manager;
    }

    @Override
    public final boolean isImmediate() {
        return true;
    }

    @Override
    void run() {
        if (manager == null) {
            throw new RuntimeException("ActionManager not assigned for the ActionFlow");
        }

    }

}
