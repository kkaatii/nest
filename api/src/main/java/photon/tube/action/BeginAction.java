package photon.tube.action;

import java.util.Objects;
import java.util.function.Supplier;

public class BeginAction<T> extends Transformation<Void, T> {

    private static final ActionManager DEFAULT_MANAGER = ActionManager.getInstance();
    private static final Supplier<Void> DEFAULT_SUPPLIER = () -> null;

    private ActionManager manager;
    private Supplier<T> supplier;

    public BeginAction(ActionManager manager, Supplier<T> supplier) {
        setManager(manager);
        this.supplier = supplier;
    }

    public BeginAction(Supplier<T> supplier) {
        this(DEFAULT_MANAGER, supplier);
    }

    @SuppressWarnings("unchecked")
    public BeginAction() {
        this(DEFAULT_MANAGER, (Supplier<T>) DEFAULT_SUPPLIER);
    }

    public void setManager(ActionManager manager) {
        Objects.requireNonNull(manager);
        this.manager = manager;
    }

    @Override
    protected T transform(Void input) {
        return supplier.get();
    }

    public void perform() {
        perform(manager);
    }

    @Override
    public boolean isImmediate() {
        return true;
    }

    protected final ActionManager manager() {
        return manager;
    }

    public static <U> BeginAction<U> wrap(Transformation<? super U, ?> action, Supplier<U> inputSupplier) {
            return action.waitFor(new BeginAction<>(inputSupplier));
    }

    public static BeginAction<Void> wrap(Transformation<Void, ?> action) {
        return wrap(action, DEFAULT_SUPPLIER);
    }

}
