package photon.action;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The <tt>ActionScheduler</tt> is responsible for executing all actions.
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

    private void withdraw() {
        if (submitted.decrementAndGet() == 0)
            tryShutdown();
    }

    private void tryShutdown() {
        if (submitted.get() == 0 && stage.get() == SHUTDOWN)
            executor.shutdown();
    }

    public void shutdown() {
        for (; ; ) {
            if (stage.get() == SHUTDOWN)
                return;
            if (stage.compareAndSet(WORKING, SHUTDOWN)) {
                tryShutdown();
                return;
            }
        }
    }

    public void submit(final Action action) {
        submit(action, ExceptionListener.DEFAULT);
    }

    public void submit(final Action action, final ExceptionListener exceptionListener) {
        if (action == null)
            throw new IllegalArgumentException();

        submitted.incrementAndGet();
        if (stage.get() == WORKING) {
            Action a = action;
            Action predecessor;
            while ((predecessor = a.predecessor()) != null) {
                a = predecessor;
                a.queue();
            }
            _submit(a, exceptionListener);
        } else withdraw();
    }

    private void _submit(final Action action, final ExceptionListener el) {
        executor.submit(new ActionRunnable(action, el));
    }

    final class ActionRunnable implements Runnable {
        private final Action headAction;
        private final ExceptionListener el;

        ActionRunnable(Action headAction, ExceptionListener el) {
            this.headAction = headAction;
            this.el = el;
        }

        @Override
        public void run() {
            Action a = headAction;
            try {
                a.run();
                a.finish();
                while ((a = a.successor()) != null) {
                    a.run();
                    a.finish();
                }
            } catch (Exception e) {
                abort(a, el, e);
            } finally {
                withdraw();
            }
        }
    }
}
