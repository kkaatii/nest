package photon.action;

import java.util.Objects;
import java.util.function.Supplier;

public abstract class Producer<T> extends Action {
    public abstract T output();

    public static <V> Producer<V> of(Supplier<V> supplier) {
        Objects.requireNonNull(supplier);
        return new Producer<V>() {
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
