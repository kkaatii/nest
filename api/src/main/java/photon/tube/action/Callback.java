package photon.tube.action;

/**
 * Created by Dun Liu on 2/23/2017.
 */
public interface Callback<T> {

    void onSuccess(T input);

    void onFailure();

}
