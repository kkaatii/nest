package photon.tube.action;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Dun Liu on 2/21/2017.
 */
public class ActionManager {

    public static final ActionManager INSTANCE = new ActionManager();
    private final AtomicLong nextActionId = new AtomicLong(0);
    private final Set<Long> scheduled = ConcurrentHashMap.newKeySet();
    private final ExecutorService executor = Executors.newWorkStealingPool();

    private ActionManager() {
    }

    public long newActionId() {
        return nextActionId.getAndIncrement();
    }

    public void schedule(Actionable action) {
        if (action != null) {
            Long id = action.id();
            if (!scheduled.contains(id)) {
                scheduled.add(id);
                executor.submit(new ActionAbortNotifier(action));
            }
        }
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
                while ((action = action.subsequent()) != null && action instanceof ImmediatelyRunnable ) {
                    ((ImmediatelyRunnable) action).runImmediately();
                }
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
