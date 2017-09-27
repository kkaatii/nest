package photon.action;

import photon.ExceptionListener;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The <tt>ActionScheduler</tt> is responsible for executing all actions. It will run those
 * immediate actions right away when the <tt>submit(Action, ExceptionListener)</tt> method is called, and
 * put the other actions into a thread pool.
 */
public class ActionScheduler {

    private static byte WORKING = 0;
    private static byte SHUTDOWN = 1;
    private static byte TERMINATED = 2;

    private byte stage = WORKING;
    private final ConcurrentMap<Long, Boolean> submitted = new ConcurrentHashMap<>();
    private final ExecutorService executor;

    private ActionScheduler(int poolSize) {
        executor = Executors.newFixedThreadPool(poolSize);
    }

    private static class SingletonHelper {
        private static final ActionScheduler INSTANCE = new ActionScheduler(4);
    }

    public static ActionScheduler getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private enum RunDecision {
        ERR, DO_NOTHING, RUN_THIS_OR_PREDECESSOR, RUN_SUCCESSOR,
    }

    private static RunDecision makeRunDecision(Action action) {
        if (action != null) {
            switch (action.state()) {
                case NOT_STARTED:
                case ABORTED:
                    return RunDecision.RUN_THIS_OR_PREDECESSOR;
                case FINISHED:
                    if (action.shouldAlwaysRerun())
                        return RunDecision.RUN_THIS_OR_PREDECESSOR;
                    else
                        return RunDecision.RUN_SUCCESSOR;
                case QUEUEING:
                    return RunDecision.DO_NOTHING;
            }
        }
        return RunDecision.ERR;
    }

    private void runAndFinish(Action action) {
        action.run();
        action.finish();
    }

    public void submit(Action action) {
        submit(action, ExceptionListener.DEFAULT);
    }

    public void submit(Action action, ExceptionListener el) {
        if (stage == WORKING) {
            RunDecision decision = makeRunDecision(action);
            if (decision == RunDecision.RUN_THIS_OR_PREDECESSOR) {
                Action newAction = action;
                Action predecessor = newAction.predecessor();
                while ((decision = makeRunDecision(predecessor)) == RunDecision.RUN_THIS_OR_PREDECESSOR) {
                    newAction = predecessor;
                    predecessor = newAction.predecessor();
                }
                if (predecessor == null || decision == RunDecision.RUN_SUCCESSOR) {
                    doRun(newAction, el);
                }
            } else if (decision == RunDecision.RUN_SUCCESSOR) {
                Action successor = action.successor();
                while ((decision = makeRunDecision(successor)) == RunDecision.RUN_SUCCESSOR) {
                    successor = successor.successor();
                }
                if (decision == RunDecision.RUN_THIS_OR_PREDECESSOR) {
                    doRun(successor, el);
                }
            }
        }
    }

    public void shutdown() {
        if (submitted.isEmpty()) {
            stage = TERMINATED;
            executor.shutdown();
        } else {
            stage = SHUTDOWN;
        }
    }

    private void doRun(Action action, ExceptionListener el) {
        if (action.isImmediate()) {
            try {
                runAndFinish(action);
                while ((action = action.successor()) != null && action.isImmediate()) {
                    runAndFinish(action);
                }
                if (action == null) return;
            } catch (Exception e) {
                doAbort(action, el, e);
                return;
            }
        }
        internalSubmit(action, el);
    }

    private void doAbort(Action action, ExceptionListener el, Exception e) {
        Action a = action;
        do {
            action.abort();
        } while ((action = action.successor()) != null);

        if (el != null) {
            ActionRuntimeException re = (e instanceof ActionRuntimeException)
                    ? (ActionRuntimeException) e
                    : new ActionRuntimeException(a, e);
            el.onException(re);
        }
    }

    private void internalSubmit(Action action, ExceptionListener el) {
        if (submitted.putIfAbsent(action.id(), Boolean.TRUE) == null) {
            action.queue();
            executor.submit(new ActionWrapper(action, el));
        }
    }

    private void withdraw(long actionId) {
        submitted.remove(actionId);
        if (stage == SHUTDOWN && submitted.isEmpty()) {
            stage = TERMINATED;
            executor.shutdown();
        }
    }

    private class ActionWrapper implements Runnable {
        private Action action;
        private final ExceptionListener el;

        ActionWrapper(Action action, ExceptionListener el) {
            this.action = action;
            this.el = el;
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
                    internalSubmit(action, el);
                }
            } catch (Exception e) {
                doAbort(action, el, e);
            } finally {
                withdraw(headId);
            }
        }
    }

}
