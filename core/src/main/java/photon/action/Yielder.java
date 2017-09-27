package photon.action;

@FunctionalInterface
public interface Yielder<T> {
    T output();
}
