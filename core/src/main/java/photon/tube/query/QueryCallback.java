package photon.tube.query;

import photon.tube.action.ActionException;
import photon.tube.action.ExceptionAlert;

/**
 * Deferred processing of the result of an <tt>Action</tt> chain. Also acts as the <tt>ExceptionAlert</tt> for
 * the <tt>Action</tt> chain.
 */
public interface QueryCallback<T> extends ExceptionAlert {

    void onSuccess(T input);

    @Override
    default void onException(ActionException ae) {
        ae.printStackTrace();
    }

}
