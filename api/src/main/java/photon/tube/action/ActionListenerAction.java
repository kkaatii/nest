package photon.tube.action;


import javax.validation.constraints.NotNull;

/**
 * Created by Dun Liu on 2/22/2017.
 */
public class ActionListenerAction<Result> extends Action<Result, Void> implements ImmediatelyRunnable {

    private final ActionListener<? super Result> listener;

    protected ActionListenerAction(ActionManager manager, @NotNull ActionListener<? super Result> listener) {
        super(manager);
        this.listener = listener;
    }

    @Override
    public void abort() {
        status = RunningStatus.NOT_STARTED;
        listener.onFailure();
    }

    @Override
    protected Void doRun(Result input) {
        listener.onSuccess(input);
        return null;
    }

}
