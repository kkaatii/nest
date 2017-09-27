package photon.action;

import java.util.concurrent.atomic.AtomicLong;

public abstract class Action {

    private static final AtomicLong PREVIOUS_ID = new AtomicLong();

    private static long _generateId() {
        long now = System.currentTimeMillis();
        while (true) {
            long lastTime = PREVIOUS_ID.get();
            if (lastTime >= now)
                now = lastTime + 1;
            if (PREVIOUS_ID.compareAndSet(lastTime, now))
                return now;
        }
    }

    enum ActionState {
        FINISHED, QUEUEING, ABORTED, NOT_STARTED
    }

    private final long id = _generateId();
    private Action predecessor;
    private Action successor;
    private ActionState state = ActionState.NOT_STARTED;
    private boolean alwaysRerun = false;
    private boolean immediate = false;

    final long id() {
        return id;
    }

    protected void setPredecessor(Action predecessor) {
        this.predecessor = predecessor;
    }

    protected void setSuccessor(Action successor) {
        this.successor = successor;
    }

    public final Action predecessor() {
        return predecessor;
    }

    public final Action successor() {
        return successor;
    }

    public <ActionType extends Action> ActionType waitFor(ActionType pred) {
        if (this.equals(pred))
            throw new RuntimeException("Action " + id + " cannot wait for itself!");
        if (predecessor != null)
            predecessor.successor = null;
        predecessor = pred;
        if (pred != null)
            predecessor.successor = this;
        state = ActionState.NOT_STARTED;
        Action then = this;
        while ((then = then.successor) != null) {
            then.state = ActionState.NOT_STARTED;
        }
        return pred;
    }

    public <ActionType extends Action> ActionType then(ActionType succ) {
        if (this.equals(succ))
            throw new RuntimeException("Action " + id + " cannot wait for itself");
        if (successor != null)
            successor.predecessor = null;
        successor = succ;
        if (succ != null) {
            successor.predecessor = this;
            successor.state = ActionState.NOT_STARTED;
            Action then = succ;
            while ((then = then.successor) != null) {
                then.state = ActionState.NOT_STARTED;
            }
        }
        return succ;
    }

    /**
     * Most {@code Action}s are supposed to run once only, so the default return of this method is {@code false}.
     *
     * @return {@code true} if the {@code Action} is designed to always re-run, {@code false} if not.
     */
    public final boolean shouldAlwaysRerun() {
        return alwaysRerun;
    }

    public final Action setAlwaysRerun(boolean b) {
        alwaysRerun = b;
        return this;
    }

    /**
     * {@code Action}s are NOT immediate by default, which means they will NOT be executed right away.
     *
     * @return {@code true} if the {@code Action} is immediate, {@code false} if not
     */
    public final boolean isImmediate() {
        return immediate;
    }

    public final Action setImmediate(boolean b) {
        immediate = b;
        return this;
    }

    synchronized final ActionState state() {
        return state;
    }

    synchronized final void finish() {
        state = ActionState.FINISHED;
    }

    synchronized final void queue() {
        state = ActionState.QUEUEING;
    }

    synchronized final void abort() {
        state = ActionState.ABORTED;
    }

    public final boolean isFinished() {
        return ActionState.FINISHED.equals(state);
    }

    public final boolean isQueueing() {
        return ActionState.QUEUEING.equals(state);
    }

    public final boolean isAborted() {
        return ActionState.ABORTED.equals(state);
    }

    abstract void run();
}
