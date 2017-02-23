package photon.tube.action;

/**
 * Created by Dun Liu on 2/20/2017.
 */
public abstract class Action<T, R> implements Actionable {

    private final ActionManager manager;
    private final long id;
    protected R result;

    protected Action<?, ? extends T> antecedent;
    protected Action<? super R, ?> subsequent;
    protected PerformStrategy performStrategy = PerformStrategy.CACHE_FIRST;
    protected volatile RunningStatus status = RunningStatus.NOT_STARTED;

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
    public Action<?, ? extends T> antecedent() {
        return antecedent;
    }

    @Override
    public Action<? super R, ?> subsequent() {
        return subsequent;
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

    public void waitFor(Action<?, ? extends T> antecedent) {
        if (this.equals(antecedent))
            throw new RuntimeException("Action " + id + " cannot wait for itself!");
        this.antecedent = antecedent;
        antecedent.subsequent = this;
    }

    public void then(Action<? super R, ?> subsequent) {
        if (this.equals(subsequent))
            throw new RuntimeException("Action " + id + " cannot wait for itself!");
        subsequent.waitFor(this);
    }

    public void perform() {
        if (!isQueueing()) {
            if (!isDone() || PerformStrategy.FORCE_UPDATE.equals(performStrategy)) {
                if (antecedent != null && !antecedent.isDone()) {
                    antecedent.perform();
                } else {
                    manager.schedule(this);
                }
            }
        }
    }

    @Override
    public final boolean isDone() {
        return RunningStatus.DONE.equals(status);
    }

    @Override
    public final boolean isAborted() {
        return RunningStatus.ABORTED.equals(status);
    }

    @Override
    public final boolean isQueueing() {
        return RunningStatus.QUEUEING.equals(status);
    }

    /**
     * This method shall be called only by the <tt>ActionManager</tt>
     */
    @Override
    public void run() {
        result = doRun(antecedent == null ? null : antecedent.result);
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
     * This method gets called only when the antecedent is successfully done.
     *
     * @param input input of the action
     * @return output of the action
     */
    protected abstract R doRun(T input);

}
