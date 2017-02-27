package photon.tube.action;

import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The <tt>ActionManager</tt> is responsible for executing all actions in a thread pool. It will run those
 * <tt>ImmediatelyActionable</tt> right when the <tt>schedule()</tt> method is called, and
 * put the other actions into the thread pool.
 */
class ActionManager {

    static final ActionManager INSTANCE = new ActionManager();
    private final AtomicLong nextActionId = new AtomicLong(0);
    private final Set<Long> scheduled = ConcurrentHashMap.newKeySet();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    private ActionManager() {
    }

    public void shutdown() throws InterruptedException {
        executor.shutdown();
    }

    long newActionId() {
        return nextActionId.getAndIncrement();
    }

    void schedule(@NotNull Actionable action) {
        if (action instanceof ImmediatelyActionable) {
            try {
                action.run();
                while ((action = action.successor()) != null && action instanceof ImmediatelyActionable) {
                    action.run();
                }
                if (action == null) return;
            } catch (Exception e) {
                onAbort(action, e);
            }
        }
        Long id = action.id();
        if (!scheduled.contains(id)) {
            scheduled.add(id);
            action.queue();
            executor.submit(new ActionContainer(action));
        }
    }

    private void onAbort(Actionable action, Exception e) {
        action.abort();
        Actionable then = action;
        while ((then = then.successor()) != null) {
            then.abort();
        }

        // TODO exception handling, logging, etc.
        action.onException(e);
    }

    private class ActionContainer implements Runnable {
        private Actionable action;

        ActionContainer(Actionable action) {
            this.action = action;
        }

        @Override
        public void run() {
            long originalActionId = action.id();
            try {
                action.run();
                while ((action = action.successor()) != null && action instanceof ImmediatelyActionable) {
                    action.run();
                }
                if (action != null) {
                    schedule(action);
                }
            } catch (Exception e) {
                onAbort(action, e);
            } finally {
                scheduled.remove(originalActionId);
            }
        }
    }

}
