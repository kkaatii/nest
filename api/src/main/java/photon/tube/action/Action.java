package photon.tube.action;

import photon.tube.query.Query;

/**
 * Created by Dun Liu on 2/20/2017.
 */
public abstract class Action<T, R> implements Actionable {

    private final ActionManager manager;
    private final long id;
    protected R result;

    protected Query query;
    protected Action<?, ? extends T> predecessor;
    protected Action<? super R, ?> successor;
    // TODO why volatile???
    protected RunningStatus status = RunningStatus.NOT_STARTED;
    protected PerformStrategy performStrategy = PerformStrategy.CACHE_FIRST;

    protected enum RunningStatus {
        DONE, QUEUEING, ABORTED, NOT_STARTED
    }

    protected Action(ActionManager manager) {
        this.manager = manager;
        this.id = manager.newActionId();
    }

    protected final ActionManager manager() {
        return manager;
    }

    @Override
    public final long id() {
        return id;
    }

    @Override
    public Action<? super R, ?> successor() {
        return successor;
    }

    public Action<?, ? extends T> predecessor() {
        return predecessor;
    }

    public final PerformStrategy performStrategy() {
        return performStrategy;
    }

    public final void setPerformStrategy(PerformStrategy performStrategy) {
        this.performStrategy = performStrategy;
    }

    public R result() {
        return result;
    }

    public void waitFor(Action<?, ? extends T> predecessor) {
        if (this.equals(predecessor))
            throw new RuntimeException("Action " + id + " cannot wait for itself!");
        if (this.predecessor != null)
            this.predecessor.successor = null;
        this.predecessor = predecessor;
        if (predecessor != null)
            predecessor.successor = this;
        status = RunningStatus.NOT_STARTED;
        Action<?, ?> then = this;
        while ((then = then.successor) != null) {
            then.status = RunningStatus.NOT_STARTED;
        }
    }

    public void then(Action<? super R, ?> successor) {
        if (this.equals(successor))
            throw new RuntimeException("Action " + id + " cannot wait for itself!");
        if (this.successor != null)
            this.successor.predecessor = null;
        this.successor = successor;
        if (successor != null) {
            successor.predecessor = this;
            successor.status = RunningStatus.NOT_STARTED;
            Action<?, ?> then = successor;
            while ((then = then.successor) != null) {
                then.status = RunningStatus.NOT_STARTED;
            }
        }
    }

    public final void perform() {
        if (!isQueueing() && (!isDone() || PerformStrategy.FORCE_UPDATE.equals(performStrategy))) {
            boolean queued = false;
            if (predecessor != null) {
                switch (predecessor.status) {
                    case QUEUEING:
                        return;
                    case DONE:
                        break;
                    default:
                        predecessor.perform();
                        queued = true;
                }
            }
            if (!queued) {
                manager.schedule(this);
            }
        }
    }

    public final boolean isDone() {
        return RunningStatus.DONE.equals(status);
    }

    public final boolean isAborted() {
        return RunningStatus.ABORTED.equals(status);
    }

    public final boolean isQueueing() {
        return RunningStatus.QUEUEING.equals(status);
    }

    /**
     * This method shall be called only by the <tt>ActionManager</tt>
     */
    @Override
    public void run() {
        result = doRun(predecessor == null ? null : predecessor.result);
        status = RunningStatus.DONE;
    }

    /**
     * This method shall be called only by the <tt>ActionManager</tt>
     */
    @Override
    public void abort() {
        status = RunningStatus.ABORTED;
    }

    /**
     * This method shall be called only by the <tt>ActionManager</tt>
     */
    @Override
    public void queue() {
        status = RunningStatus.QUEUEING;
    }

    /**
     * This method shall be called only by the <tt>ActionManager</tt>
     */
    @Override
    public void onException(Exception e) {
        if (query != null) {
            query.onException(new ActionRuntimeException(this, e));
        }
    }

    /**
     * This method gets called only when the predecessor is successfully done.
     *
     * @param input input of the action
     * @return output of the action
     */
    protected abstract R doRun(T input);

}
