package photon.action;

import photon.util.PiEstimateAggregator;
import photon.util.Stopwatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ActionTest {

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
        ActionTest test = new ActionTest();

        // Warm up
        stopwatchForNative.start();
        double sum = 0;
        for (int i = 0; i < 1; i++) {
            sum += test.calculatePi(roundPerTrial * trials);
        }
        stopwatchForNative.stop();
        System.out.println("Elapsed time (milliseconds): " + stopwatchForNative.millis());
        System.out.println("Estimated Pi is " + 4 * sum / 1);

        // Test on Action
        ActionScheduler scheduler = ActionScheduler.getInstance();
        Stopwatch stopwatchForAction = new Stopwatch() {
            @Override
            public void stop() {
                super.stop();
                scheduler.shutdown();
            }
        };
        PiEstimateAggregator aggregator = new PiEstimateAggregator(trials, stopwatchForAction);
        List<Action> actions = new ArrayList<>();
        for (int i = 0; i < trials; i++) {
            ActionTest.NumSupplier supplier = new ActionTest.NumSupplier(roundPerTrial);
            actions.add(supplier
                    .then(Transformation.of(test::calculatePi))
                    .then(Transformation.of(aggregator::add).setAlwaysRerun(true))
            );
        }
        System.out.println("---------------  Begin test on Action  ---------------");
        stopwatchForAction.start();
        for (Action action : actions) {
            scheduler.submit(action);
        }

//        // Warm up again
//        Thread.sleep(2000);
//        stopwatchForNative.start();
//        sum = 0;
//        for (int i = 0; i < 1; i++) {
//            sum += test.calculatePi(roundPerTrial*trials);
//        }
//        stopwatchForNative.stop();
//        System.out.println("Estimated Pi is " + 4 * sum / 1);
//        System.out.println("Elapsed time (milliseconds): " + stopwatchForNative.millis());
//
//        // Test on native Thread
//        Thread.sleep(5000);
//        PiEstimateAggregator counter2 = new PiEstimateAggregator(trials, stopwatchForNative);
//        List<Runnable> runnables = new ArrayList<>();
//        ExecutorService service = Executors.newFixedThreadPool(4);
//        for (int i = 0; i < trials; i++) {
//            ActionTest.NumSupplier supplier = new ActionTest.NumSupplier(roundPerTrial);
//            runnables.add(() -> counter2.add(test.calculatePi(supplier.output())));
//        }
//        System.out.println("---------------  Begin test on native thread  ---------------");
//        stopwatchForNative.start();
//        for (Runnable runnable : runnables) {
//            service.submit(runnable);
//        }
//        service.shutdown();

    }


}
