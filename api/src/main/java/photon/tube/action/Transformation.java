package photon.tube.action;

import java.util.function.Function;

public abstract class Transformation<InputT, OutputT> extends Action {

    protected OutputT output;
    public OutputT output() {
        return output;
    }

    @SuppressWarnings("unchecked")
    @Override
    final void run() {
        if (predecessor == null) {
            output = transform(null);
            state = RunningState.DONE;
        } else if (predecessor instanceof Transformation<?, ?>) {
            output = transform(((Transformation<?, InputT>) predecessor).output());
            state = RunningState.DONE;
        } else throw new RuntimeException("Input from the predecessor is expected but missing!");
    }

    /**
     * This method gets called only when the predecessor is successfully done.
     *
     * @param input input for the transformation
     * @return output of the transformation
     */
    protected abstract OutputT transform(InputT input);

    public static <U, V> Transformation<U, V> of(Function<U, V> function) {
        return new Transformation<U, V>() {
            @Override
            protected V transform(U input) {
                return function.apply(input);
            }
        };
    }

}
