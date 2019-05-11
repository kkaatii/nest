package photon.action;

@FunctionalInterface
public interface ExceptionListener {
    ExceptionListener DEFAULT = Throwable::printStackTrace;

    void onException(Exception e);
}
