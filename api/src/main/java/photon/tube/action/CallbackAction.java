package photon.tube.action;


import javax.validation.constraints.NotNull;

/**
 * Created by Dun Liu on 2/22/2017.
 */
public class CallbackAction<Result> extends Action<Result, Void> implements ImmediatelyActionable {

    private final Callback<? super Result> listener;

    protected CallbackAction(ActionManager manager, @NotNull Callback<? super Result> listener) {
        super(manager);
        this.listener = listener;
        this.performStrategy = PerformStrategy.CACHE_FIRST;
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
