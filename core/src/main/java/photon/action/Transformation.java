package photon.action;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class Transformation<InputT, OutputT> extends Producer<OutputT> {

    private OutputT output;

    @Override
    public final OutputT output() {
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

    public static <U> Transformation<U, Void> of(Consumer<U> consumer) {
        Objects.requireNonNull(consumer);
        return new Transformation<U, Void>() {
            @Override
            protected Void transform(U input) {
                consumer.accept(input);
                return null;
            }
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    protected final void run() {
        Action predecessor = predecessor();
        if (predecessor == null || !(predecessor instanceof Producer)) {
            output = transform(null);
        } else {
            output = transform(((Producer<? extends InputT>) predecessor).output());
        }
    }

    /**
     * This method gets called only when the predecessor has successfully finished its job.
     *
     * @param input input for the transformation
     * @return output of the transformation
     */
    protected abstract OutputT transform(InputT input);

}
