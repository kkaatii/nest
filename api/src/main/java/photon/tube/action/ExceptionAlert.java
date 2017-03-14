package photon.tube.action;

/**
 * Created by Dun Liu on 2/24/2017.
 */
public interface ExceptionAlert<E extends Exception> {
    void onException(E e);
}
