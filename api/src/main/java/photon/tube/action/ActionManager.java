package photon.tube.action;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The <tt>ActionManager</tt> is responsible for executing all actions in a thread pool. It will run those
 * <tt>ImmediatelyRunnable</tt> actions right when the <tt>schedule()</tt> method is called, and
 * put the other actions into the thread pool.
 */
class ActionManager {

    static final ActionManager INSTANCE = new ActionManager();
    private final AtomicLong nextActionId = new AtomicLong(0);
    private final Set<Long> scheduled = ConcurrentHashMap.newKeySet();
    private final ExecutorService executor = Executors.newWorkStealingPool();

    private ActionManager() {
    }

    long newActionId() {
        return nextActionId.getAndIncrement();
    }

    void schedule(Actionable action) {
        if (action != null && action instanceof ImmediatelyRunnable) {
            try {
                ((ImmediatelyRunnable) action).runImmediately();
                action = tryRunningSubsequentImmediately(action);
            } catch (Exception e) {
                onAbort(action);
                throw new ActionRuntimeException(e);
            }
        }
        if (action != null) {
            Long id = action.id();
            if (!scheduled.contains(id)) {
                scheduled.add(id);
                executor.submit(new ActionAbortNotifier(action));
            }
        }

    }

    private Actionable tryRunningSubsequentImmediately(Actionable action) {
        while ((action = action.subsequent()) != null && action instanceof ImmediatelyRunnable) {
            ((ImmediatelyRunnable) action).runImmediately();
        }
        return action;
    }

    private void onAbort(Actionable action) {
        action.abort();
        while ((action = action.subsequent()) != null) {
            action.abort();
        }
        // TODO exception handling
    }

    private class ActionAbortNotifier implements Runnable {
        private Actionable action;

        ActionAbortNotifier(Actionable action) {
            this.action = action;
        }

        @Override
        public void run() {
            long originalActionId = action.id();
            try {
                action.run();
                action = tryRunningSubsequentImmediately(action);
                schedule(action);
            } catch (Exception e) {
                onAbort(action);
                throw new ActionRuntimeException(e);
            } finally {
                scheduled.remove(originalActionId);
            }
        }
    }

}
