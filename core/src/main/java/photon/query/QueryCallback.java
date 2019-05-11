package photon.query;

import photon.action.ExceptionListener;

public interface QueryCallback extends ExceptionListener {

    void onSuccess(QueryResult queryResult);

    @Override
    default void onException(Exception e) {
        e.printStackTrace();
    }

}
