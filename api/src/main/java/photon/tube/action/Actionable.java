package photon.tube.action;

/**
 * Created by Dun Liu on 2/23/2017.
 */
interface Actionable {

    long id();

    Actionable antecedent();

    Actionable subsequent();

    boolean isDone();

    boolean isAborted();

    boolean isQueueing();

    void run();

    void abort();

    void queue();

}
