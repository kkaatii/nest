package photon.tube.action;


import java.util.Objects;

public class CallbackAction<Result> extends Transformation<Result, Void> {

    private final Callback<? super Result> listener;

    public CallbackAction(Callback<? super Result> listener) {
        Objects.requireNonNull(listener);
        this.listener = listener;
    }

    @Override
    public void abort(ActionRuntimeException e) {
        super.abort(e);
        listener.onFailure();
    }

    @Override
    protected Void transform(Result input) {
        listener.onSuccess(input);
        return null;
    }

    @Override
    public boolean isImmediate() {
        return true;
    }

}
