package photon.tube.action;

@FunctionalInterface
public interface Yielder<T> {
    T output();
}
