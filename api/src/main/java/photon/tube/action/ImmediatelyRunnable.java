package photon.tube.action;

/**
 * Created by Dun Liu on 2/23/2017.
 */
public interface ImmediatelyRunnable extends Runnable {
    default void runImmediately() {
        run();
    }
}
