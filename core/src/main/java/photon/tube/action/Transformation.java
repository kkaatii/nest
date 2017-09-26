package photon.tube.action;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Transformation<InputT, OutputT> extends Action implements Yielder<OutputT> {

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

    public static <V> Transformation<Void, V> of(Supplier<V> supplier) {
        Objects.requireNonNull(supplier);
        return new Transformation<Void, V>() {
            @Override
            protected V transform(Void input) {
                return supplier.get();
            }
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    final void run() {
        Action predecessor = predecessor();
        if (predecessor == null) {
            output = transform(null);
        } else if (predecessor instanceof Transformation) {
            output = transform(((Transformation<?, InputT>) predecessor).output());
        } else throw new RuntimeException("The predecessor Action does not yield an output.");
    }

    /**
     * This method gets called only when the predecessor has successfully finished its job.
     *
     * @param input input for the transformation
     * @return output of the transformation
     */
    protected abstract OutputT transform(InputT input);

}
