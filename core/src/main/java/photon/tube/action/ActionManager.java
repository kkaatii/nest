package photon.tube.action;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The <tt>ActionManager</tt> is responsible for executing all actions. It will run those
 * immediate actions right away when the <tt>schedule()</tt> method is called, and
 * put the other actions into a thread pool.
 */
public class ActionManager {

    private boolean waitingForShutdown = false;
    private final ConcurrentMap<Long, Boolean> submitted = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

//    private ActionManager() {
//    }

    private static class SingletonHelper {
        private static final ActionManager INSTANCE = new ActionManager();
    }

    public static ActionManager getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private enum RunDecision {
        ERR, TERMINATE, RUN_THIS_OR_PREDECESSOR, RUN_SUCCESSOR,

    }

    private static RunDecision makeRunDecision(Action action) {
        if (action != null) {
            switch (action.state()) {
                case NOT_STARTED:
                case ABORTED:
                    return RunDecision.RUN_THIS_OR_PREDECESSOR;
                case FINISHED:
                    if (action.isAlwaysRerun())
                        return RunDecision.RUN_THIS_OR_PREDECESSOR;
                    else
                        return RunDecision.RUN_SUCCESSOR;
                case QUEUEING:
                    return RunDecision.TERMINATE;
            }
        }
        return RunDecision.ERR;
    }

    private void runAndFinish(Action action) {
        action.run();
        action.finish();
    }

    public void schedule(Action action) {
        schedule(action, ExceptionAlert.defaultAlert());
    }

    public void schedule(Action action, ExceptionAlert ea) {
        RunDecision decision = makeRunDecision(action);
        if (decision == RunDecision.RUN_THIS_OR_PREDECESSOR) {
            Action newAction = action;
            Action predecessor = newAction.predecessor();
            while ((decision = makeRunDecision(predecessor)) == RunDecision.RUN_THIS_OR_PREDECESSOR) {
                newAction = predecessor;
                predecessor = newAction.predecessor();
            }
            if (predecessor == null || decision == RunDecision.RUN_SUCCESSOR) {
                doRun(newAction, ea);
            }
        } else if (decision == RunDecision.RUN_SUCCESSOR) {
            Action successor = action.successor();
            while ((decision = makeRunDecision(successor)) == RunDecision.RUN_SUCCESSOR) {
                successor = successor.successor();
            }
            if (decision == RunDecision.RUN_THIS_OR_PREDECESSOR) {
                doRun(successor, ea);
            }
        }
    }

    public void shutdown() {
        if (submitted.isEmpty()) {
            executor.shutdown();
        } else {
            waitingForShutdown = true;
        }
    }

    private void doRun(Action action, ExceptionAlert ea) {
        if (action.isImmediate()) {
            try {
                runAndFinish(action);
                while ((action = action.successor()) != null && action.isImmediate()) {
                    runAndFinish(action);
                }
                if (action == null) return;
            } catch (Exception e) {
                doAbort(action, ea, e);
                return;
            }
        }
        submit(action, ea);
    }

    private void doAbort(Action action, ExceptionAlert ea, Exception e) {
        Action a = action;
        do {
            action.abort();
        } while ((action = action.successor()) != null);

        if (ea != null) {
            ActionException re = new ActionException(a, e);
            ea.onException(re);
        }
    }

    private void submit(Action action, ExceptionAlert ea) {
        if (submitted.putIfAbsent(action.id(), Boolean.TRUE) == null) {
            action.queue();
            executor.submit(new ActionWrapper(action, ea));
        }
    }

    private void withdraw(long actionId) {
        submitted.remove(actionId);
        if (waitingForShutdown && submitted.isEmpty()) {
            executor.shutdown();
        }
    }

    private class ActionWrapper implements Runnable {
        private Action action;
        private final ExceptionAlert alert;

        ActionWrapper(Action action, ExceptionAlert alert) {
            this.action = action;
            this.alert = alert;
        }

        @Override
        public void run() {
            long headId = action.id();
            try {
                runAndFinish(action);
                while ((action = action.successor()) != null && action.isImmediate()) {
                    runAndFinish(action);
                }
                if (action != null) {
                    submit(action, alert);
                }
            } catch (Exception e) {
                doAbort(action, alert, e);
            } finally {
                withdraw(headId);
            }
        }
    }

}
