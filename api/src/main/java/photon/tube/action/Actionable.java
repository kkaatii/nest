package photon.tube.action;

/**
 * Created by Dun Liu on 2/23/2017.
 */
interface Actionable extends ActionExceptionHandler<Exception> {

    long id();

    Actionable successor();

    void run();

    void abort();

    void queue();

}
