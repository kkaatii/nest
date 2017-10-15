package photon.action;

import photon.ExceptionListener;
import photon.util.PiEstimateAggregator;
import photon.util.Stopwatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ActionTest {

    private AtomicInteger count = new AtomicInteger(0);

    public static void main(String... args) throws Exception {
        ActionTest actionTest = new ActionTest();
        actionTest.piTest(1000, 2000);
        Thread.sleep(2000);
        System.out.println(actionTest.count);
    }

    public double calculatePi(int N) {
        if (N < 0) return Double.NaN;

        double sum = 0;
        Random random1 = new Random();

        for (int j = 0; j < N; j++) {
            double x = random1.nextDouble();
            double y = random1.nextDouble();
            if (x * x + y * y < 1)
                sum++;
        }
        return (sum / N);
    }

    public static class NumSupplier extends Transformation<Void, Integer> {
        private int n;

        public NumSupplier(int n) {
            this.n = n;
            setImmediate(true);
        }

        @Override
        public Integer transform(Void _void) {
            return n;
        }

    }


    /**
     * Compare the speed of pi estimation of plain calculation vs. <code>Action</code> vs. native <code>Thread</code>
     */
    public void piTest(int trials, int roundPerTrial) throws InterruptedException {
        Stopwatch stopwatchForNative = new Stopwatch();
        Stopwatch stopwatchForAction = new Stopwatch();
        PiEstimateAggregator aggregator = new PiEstimateAggregator(trials, stopwatchForAction);
        ActionTest test = new ActionTest();

        List<Action> actions = new ArrayList<>();
        for (int i = 0; i < trials; i++) {
            ActionTest.NumSupplier supplier = new ActionTest.NumSupplier(roundPerTrial);
            supplier
                    .then(Transformation.of(test::calculatePi).setImmediate(true))
                    .then(Transformation.of(aggregator::add).setImmediate(true));
            actions.add(supplier);
        }

        // Warm up
        stopwatchForNative.start();
        double sum = 0;
        for (int i = 0; i < 1; i++) {
            sum += test.calculatePi(roundPerTrial * trials);
        }
        stopwatchForNative.stop();
        System.out.println("Elapsed time (milliseconds): " + stopwatchForNative.millis());
        System.out.println("Estimated Pi is " + 4 * sum / 1);

        // Test on native Thread
        ExecutorService service = Executors.newFixedThreadPool(4);
        ActionScheduler newScheduler = new ActionScheduler(4);
        PiEstimateAggregator aggregator2 = new PiEstimateAggregator(trials, stopwatchForNative);
        System.out.println("---------------  Begin test on native thread  ---------------");
        List<Runnable> runnables = new ArrayList<>();
        ActionTest.NumSupplier supplier = new ActionTest.NumSupplier(roundPerTrial);
        for (int i = 0; i < trials; i++) {
            runnables.add(() -> aggregator2.add(test.calculatePi(supplier.transform(null))));
        }
        aggregator.reset();
        stopwatchForAction.start();
        for (Action action : actions) {
            service.submit(new SimplifiedActionWrapper(action, ExceptionListener.DEFAULT));
        }
        service.shutdown();

        // Warm up again
        Thread.sleep(200);
        stopwatchForNative.start();
        sum = 0;
        for (int i = 0; i < 1; i++) {
            sum += test.calculatePi(roundPerTrial*trials);
        }
        stopwatchForNative.stop();
        System.out.println("Estimated Pi is " + 4 * sum / 1);
        System.out.println("Elapsed time (milliseconds): " + stopwatchForNative.millis());

        // Test on Action
        ActionScheduler scheduler = ActionScheduler.getInstance();
        System.out.println("---------------  Begin test on Action  ---------------");
        aggregator.reset();
        stopwatchForAction.start();
        for (Action action : actions) {
            scheduler.submit(action, ExceptionListener.DEFAULT);
        }
//        scheduler.shutdown();

    }

    class SimplifiedActionWrapper implements Runnable {
        private Action action;
        private ExceptionListener listener;

        SimplifiedActionWrapper(Action action, ExceptionListener listener) {
            this.action = action;
            this.listener = listener;
        }

        @Override
        public void run() {
            try {
                count.incrementAndGet();
                action.run();
                action.finish();
                Action a = action;
                while ((a = a.successor()) != null && a.isImmediate()) {
                    action = a;
                    a.queue();
                    a.run();
                    a.finish();
                }
            } catch (Exception e) {
                listener.onException(e);
            }
        }
    }

}
