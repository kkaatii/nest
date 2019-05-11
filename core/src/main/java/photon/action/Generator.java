package photon.action;

import java.util.Objects;
import java.util.function.Supplier;

public abstract class Generator<T> extends Action {
    public abstract T output();

    public static <V> Generator<V> of(Supplier<V> supplier) {
        Objects.requireNonNull(supplier);
        return new Generator<V>() {
            private V output;

            @Override
            public V output() {
                return output;
            }

            @Override
            protected void run() {
                output =  supplier.get();
            }
        };
    }
}
