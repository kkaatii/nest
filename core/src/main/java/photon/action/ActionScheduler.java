package photon.action;

import photon.ExceptionListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The <tt>ActionScheduler</tt> is responsible for executing all actions. It will run those
 * immediate actions right away when <tt>submit(Action, ExceptionListener)</tt> is called, and
 * put the other actions into a thread pool.
 */
public class ActionScheduler {

    private static final int WORKING = 0;
    private static final int SHUTDOWN = -1;

    private final AtomicInteger stage = new AtomicInteger(WORKING);
    private final AtomicInteger submitted = new AtomicInteger(0);
    private final ExecutorService executor;

    ActionScheduler(int poolSize) {
        executor = Executors.newFixedThreadPool(poolSize);
    }

    final class ActionRunnable implements Runnable {
        private Action action;
        private final ExceptionListener el;

        ActionRunnable(Action action, ExceptionListener el) {
            this.action = action;
            this.el = el;
        }

        @Override
        public void run() {
            try {
                action.run();
                action.finish();
                Action a = action;
                while ((a = a.successor()) != null && a.isImmediate()) {
                    action = a;
                    a.queue();
                    a.run();
                    a.finish();
                }
                if (a != null) {
                    internalSubmit(a, el);
                } else {
                    withdraw();
                }
            } catch (Exception e) {
                abort(action, el, e);
                withdraw();
            }
        }
    }

    private static class SingletonHelper {
        private static final ActionScheduler INSTANCE = new ActionScheduler(4);
    }

    public static ActionScheduler getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private static void abort(final Action action, final ExceptionListener el, Exception e) {
        Action a = action;
        do {
            a.abort();
        } while ((a = a.successor()) != null);

        if (el != null) {
            ActionRuntimeException re = (e instanceof ActionRuntimeException)
                    ? (ActionRuntimeException) e
                    : new ActionRuntimeException(action, e);
            el.onException(re);
        }
    }

    private void internalSubmit(final Action action, final ExceptionListener el) {
        action.queue();
        executor.submit(new ActionRunnable(action, el));
    }

    private void withdraw() {
        if (submitted.decrementAndGet() == 0)
            tryShutdown();
    }

    private void tryShutdown() {
        if (submitted.get() == 0 && stage.get() == SHUTDOWN)
            executor.shutdown();
    }

    public void submit(final Action action) {
        submit(action, ExceptionListener.DEFAULT);
    }

    public void submit(final Action action, final ExceptionListener exceptionListener) {
        submitted.incrementAndGet();
        if (stage.get() == WORKING) {
            Action a = action;
            Action predecessor;
            while ((predecessor = a.predecessor()) != null)
                a = predecessor;
            internalSubmit(a, exceptionListener);
        } else withdraw();
    }

    public void shutdown() {
        for (;;) {
            if (stage.get() == SHUTDOWN)
                return;
            if (stage.compareAndSet(WORKING, SHUTDOWN)) {
                tryShutdown();
                return;
            }
        }
    }
}
