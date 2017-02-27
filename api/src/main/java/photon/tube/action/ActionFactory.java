package photon.tube.action;

/**
 * Created by Dun Liu on 2/22/2017.
 */
public interface ActionFactory<ActionType extends Actionable> {
    ActionType create(ActionRequest actionRequest);
}
