package photon.tube.action;

@FunctionalInterface
public interface ExceptionAlert {
    void onException(ActionException ae);

    static ExceptionAlert defaultAlert() {
        return Throwable::printStackTrace;
    }
}
