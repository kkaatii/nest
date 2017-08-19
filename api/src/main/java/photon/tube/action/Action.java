package photon.tube.action;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Basic component which comprises a {@code Query}. Each action will be handled by an {@code ActionManager}.
 */
public abstract class Action {

    private static final AtomicLong LAST_ID = new AtomicLong();

    private static long _generateId() {
        long now = System.currentTimeMillis();
        while (true) {
            long lastTime = LAST_ID.get();
            if (lastTime >= now)
                now = lastTime + 1;
            if (LAST_ID.compareAndSet(lastTime, now))
                return now;
        }
    }

    protected enum RunningState {
        DONE, QUEUEING, ABORTED, NOT_STARTED
    }

    private final long id = _generateId();
    protected Action predecessor;
    protected Action successor;
    protected RunningState state = RunningState.NOT_STARTED;
    protected PerformStrategy performStrategy = PerformStrategy.CACHE_FIRST;
    protected ExceptionAlert<ActionRuntimeException> exceptionAlert;

    public static Action of(Runnable runnable) {
        Objects.requireNonNull(runnable);
        return new Action() {
            @Override
            void run() {
                runnable.run();
            }
        };
    }

    public Action predecessor() {
        return predecessor;
    }

    public Action successor() {
        return successor;
    }

    public <ActionType extends Action> ActionType waitFor(ActionType predecessor) {
        if (this.equals(predecessor))
            throw new RuntimeException("Action " + id + " cannot wait for itself!");
        if (this.predecessor != null)
            this.predecessor.successor = null;
        this.predecessor = predecessor;
        if (predecessor != null)
            predecessor.successor = this;
        state = RunningState.NOT_STARTED;
        Action then = this;
        while ((then = then.successor) != null) {
            then.state = RunningState.NOT_STARTED;
        }
        return predecessor;
    }

    public <ActionType extends Action> ActionType then(ActionType successor) {
        if (this.equals(successor))
            throw new RuntimeException("Action " + id + " cannot wait for itself!");
        if (this.successor != null)
            this.successor.predecessor = null;
        this.successor = successor;
        if (successor != null) {
            successor.predecessor = this;
            successor.state = RunningState.NOT_STARTED;
            Action then = successor;
            while ((then = then.successor) != null) {
                then.state = RunningState.NOT_STARTED;
            }
        }
        return successor;
    }

    public final void setPerformStrategy(PerformStrategy strategy) {
        performStrategy = strategy;
    }

    public final void setExceptionAlert(ExceptionAlert<ActionRuntimeException> alert) {
        exceptionAlert = alert;
    }

    public void perform(ActionManager manager) {
        if (!isQueueing() && (!isDone() || PerformStrategy.FORCE_UPDATE.equals(performStrategy))) {
            boolean queued = false;
            if (predecessor != null) {
                switch (predecessor.state) {
                    case QUEUEING:
                        return;
                    case DONE:
                        break;
                    default:
                        predecessor.perform(manager);
                        queued = true;
                }
            }
            if (!queued) {
                manager.schedule(this);
            }
        }
    }

    final long id() {
        return id;
    }

    abstract void run();

    void queue() {
        state = RunningState.QUEUEING;
    }

    void abort() {
        state = RunningState.ABORTED;
    }

    void abort(ActionRuntimeException e) {
        state = RunningState.ABORTED;
        if (exceptionAlert != null) {
            exceptionAlert.onException(e);
        }
    }

    boolean isImmediate() {
        return false;
    }

    boolean isDone() {
        return RunningState.DONE.equals(state);
    }

    boolean isQueueing() {
        return RunningState.QUEUEING.equals(state);
    }

    boolean isAborted() {
        return RunningState.ABORTED.equals(state);
    }

}
