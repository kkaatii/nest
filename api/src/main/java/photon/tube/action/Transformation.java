package photon.tube.action;

import java.util.Objects;
import java.util.function.Function;

public abstract class Transformation<InputT, OutputT> extends Action {

    protected OutputT output;
    public OutputT output() {
        return output;
    }

    public static <U, V> Transformation<U, V> of(Function<U, V> function) {
        Objects.requireNonNull(function);
        return new Transformation<U, V>() {
            @Override
            protected V transform(U input) {
                return function.apply(input);
            }
        };
    }

    public <U> Transformation<U, ? extends InputT> waitFor(Function<U, ? extends InputT> function) {
        return waitFor(Transformation.of(function));
    }

    public <V> Transformation<? super OutputT, V> then(Function<? super OutputT, V> function) {
        return then(Transformation.of(function));
    }

    @SuppressWarnings("unchecked")
    @Override
    final void run() {
        if (predecessor == null) {
            output = transform(null);
        } else if (predecessor instanceof Transformation<?, ?>) {
            output = transform(((Transformation<?, InputT>) predecessor).output);
        } else throw new RuntimeException("The predecessor is not a Transformation");
        state = RunningState.DONE;
    }

    /**
     * This method gets called only when the predecessor has successfully finished its job.
     *
     * @param input input for the transformation
     * @return output of the transformation
     */
    protected abstract OutputT transform(InputT input);

}
