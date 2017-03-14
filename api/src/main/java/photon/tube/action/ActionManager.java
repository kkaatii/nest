package photon.tube.action;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The <tt>ActionManager</tt> is responsible for executing all actions in a thread pool. It will run those
 * immediate actions right away when the <tt>schedule()</tt> method is called, and
 * put the other actions into the thread pool.
 */
class ActionManager {

    private final ConcurrentMap<Long, Boolean> scheduled = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    private ActionManager() {}

    private static class SingletonHelper {
        private static final ActionManager INSTANCE = new ActionManager();
    }

    public static ActionManager getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public void shutdown() throws InterruptedException {
        executor.shutdown();
    }

    void schedule(Action action) {
        if (action.isImmediate()) {
            try {
                action.run();
                while ((action = action.successor()) != null && action.isImmediate()) {
                    action.run();
                }
                if (action == null) return;
            } catch (Exception e) {
                doAbort(action, e);
            }
        }
        if (scheduled.putIfAbsent(action.id(), Boolean.TRUE) == null) {
            action.queue();
            executor.submit(new ActionWrapper(action));
        }
    }

    private void doAbort(Action action, Exception e) {
        ActionRuntimeException re = new ActionRuntimeException(action, e);
        Action then = action;
        while ((then = then.successor()) != null) {
            then.abort();
        }

        action.abort(re);
        // TODO logging, etc.
    }

    private class ActionWrapper implements Runnable {
        private Action action;

        ActionWrapper(Action action) {
            this.action = action;
        }

        @Override
        public void run() {
            long originalActionId = action.id();
            try {
                action.run();
                while ((action = action.successor()) != null && action.isImmediate()) {
                    action.run();
                }
                if (action != null) {
                    schedule(action);
                }
            } catch (Exception e) {
                doAbort(action, e);
            } finally {
                scheduled.remove(originalActionId);
            }
        }
    }

}
