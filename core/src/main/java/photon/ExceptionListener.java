package photon;

@FunctionalInterface
public interface ExceptionListener {
    ExceptionListener DEFAULT = Throwable::printStackTrace;

    void onException(Exception e);
}
