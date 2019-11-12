package photon.action;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dan on 25/09/2017.
 */
// TODO finish ActionPlan
public class ActionPlan {

    private final Map<String, Action> stageHeads = new HashMap<>();
    private final Map<String, String> dependencyMap = new HashMap<>();
    private boolean submitted;

    private static Action headOf(Action action) {
        if (action == null) {
            return null;
        }
        Action head = action;
        while ((action = action.predecessor()) != null) {
            head = action;
        }
        return head;
    }

    private static Action tailOf(Action action) {
        if (action == null) {
            return null;
        }
        Action tail = action;
        while ((action = action.successor()) != null) {
            tail = action;
        }
        return tail;
    }

    public ActionPlan() {

    }

    synchronized public boolean trySubmit() {
        if (submitted)
            return false;
        else {
            submitted = true;
            return true;
        }
    }

    private static class StageNotice<U> extends Transformation<U, Void> {

        private final StageAdapter adapter;
        private final int ord;

        StageNotice(StageAdapter adapter, int ord) {
            this.adapter = adapter;
            this.ord = ord;
        }

        @Override
        protected Void transform(U input) {
            if (adapter.store(ord, input))
                setSuccessor(adapter);
            return null;
        }
    }

    private class StageAdapter extends Producer<Object[]> {
        private final Object[] storage;
        private final AtomicInteger counter;

        StageAdapter(int forks) {
            counter = new AtomicInteger(forks);
            storage = new Object[forks];
            finish();
        }

        /**
         * Put the result of a preceding stage into the {@code Object[]} which will be passed to the next stages.
         *
         * @param index which position in the {@code Object[]} to place the result
         * @param item  result of the stage to be stored
         * @return {@code true} if all the dependencies are finished
         */
        boolean store(int index, Object item) {
            storage[index] = item;
            return counter.decrementAndGet() == 0;
        }

        @Override
        public Object[] output() {
            return storage;
        }

        @Override
        protected void run() {
        }
    }
}
