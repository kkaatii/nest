package photon;

public interface Callback<T> extends ExceptionListener {

    void onSuccess(T outcome);

    @Override
    default void onException(Exception e) {
        e.printStackTrace();
    }

}
