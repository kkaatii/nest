package photon.tube.action;

import org.apache.commons.lang3.time.StopWatch;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * Created by Dun Liu on 2/23/2017.
 */
public class ActionTest {

    static class NumProvider extends Action<Void, Integer> implements ImmediatelyActionable {
        private int n;

        public NumProvider(ActionManager manager, int n) {
            super(manager);
            this.n = n;
        }

        @Override
        public Integer doRun(Void _void) {
            return n;
        }
    }

    static class PiCalculator extends Action<Void, Double> {
        private final int n;

        public PiCalculator(ActionManager manager, int n) {
            super(manager);
            this.n = n;
            setPerformStrategy(PerformStrategy.FORCE_UPDATE);
        }

        @Override
        public Double doRun(Void _void) {
            double sum = 0;
            Random random = new SecureRandom();
            for (int i = 0; i < n; i++) {
                double x = random.nextDouble();
                double y = random.nextDouble();
                if (x * x + y * y < 1)
                    sum++;
            }
            return sum / n;
        }

        @Override
        public void onException(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    static class PredecessorInspector extends Action<Void, Void> implements ImmediatelyActionable {
        protected PredecessorInspector(ActionManager manager) {
            super(manager);
        }

        @Override
        public void abort() {
            super.abort();
            Action<?, ?> prev = predecessor;
            while (prev != null) {
                System.out.println(prev.status);
                prev = prev.predecessor;
            }
        }

        @Override
        protected Void doRun(Void input) {
            return null;
        }
    }

    static class Average {
        public final int id;
        public final int size;
        public final double[] values;
        private int count = 0;
        private final Lock lock = new ReentrantLock();
        private StopWatch watch;

        public Average(int id, int size, StopWatch watch) {
            this.id = id;
            this.size = size;
            this.values = new double[size];
            this.watch = watch;
        }

        public void addValue(double value) {
            lock.lock();
            values[count++] = value;
            if (count == size)
                print();
            lock.unlock();
        }

        private void print() {
            double total = 0;
            for (int i = 0; i < size; i++) {
                total += values[i];
            }
            watch.suspend();
            //System.out.println("The average is " + total / size * 4);

            count = 0;
           // System.out.println("The actions " + id + " take " + watch.getTime() + " milliseconds");
        }
    }

    static class PiCalc implements Supplier<Double> {

        public final int N;

        public PiCalc(int n) {
            N = n;
        }
        @Override
        public Double get() {
            double sum = 0;
            Random random1 = new SecureRandom();

            for (int j = 0; j < N; j++) {
                double x = random1.nextDouble();
                double y = random1.nextDouble();
                if (x * x + y * y < 1)
                    sum++;
            }
            return (sum / N);
        }
    }

    public void test() throws Exception {
        int N = 20000;
        int times = 4;
        List<Action<Void, Double>> numList = new ArrayList<>();

        StopWatch watch = new StopWatch();
        Average average = new Average(0, times, watch);

        for (int i = 0; i < times; i++) {
            Action<Void, Integer> num = new NumProvider(ActionManager.INSTANCE, N);
            Action<Void, Double> piCalc = new PiCalculator(ActionManager.INSTANCE, N);
            CallbackAction<Double> write = new CallbackAction<>(ActionManager.INSTANCE, new Callback<Double>() {
                @Override
                public void onSuccess(Double value) {
                    average.addValue(value);
                }

                @Override
                public void onFailure() {
                }
            });
            PredecessorInspector inspector = new PredecessorInspector(ActionManager.INSTANCE);
            piCalc.then(write);
            numList.add(piCalc);
        }

        // Warm-up JVM
        System.out.println(calculatePi(N * times));

        // Warm-up again
        for (int i = 0; i < 3; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    calculatePi(N*10);
                }
            }).start();
        }
        Thread.sleep(2000L);

        // Test actions
        watch.start();
        watch.suspend();
        for (int i = 0; i < 1; i++) {
            watch.resume();
            for (Action<?, ?> actionable : numList) {
                actionable.perform();
            }
            Thread.sleep(100L);
        }
        watch.stop();
        System.out.println(watch.getTime());
        numList.get(0).manager().shutdown();

        // Test plain calculation
        /*ExecutorService executor = Executors.newFixedThreadPool(2);
        StopWatch watch1 = new StopWatch();
        Average average1 = new Average(1, times, watch1);
        Supplier<Double> supplier = new PiCalc(N);
        List<Supplier<Double>> funcList = new ArrayList<>();
        for (int i = 0; i < times; i++) {
            funcList.add(supplier);
        }
        watch1.start();
        watch1.suspend();
        for (int i = 0; i < 1; i++) {
            watch1.resume();
            for (Supplier<Double> supplier1 : funcList) {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        average1.addValue(supplier1.get());
                    }
                });
            }
            Thread.sleep(100L);
        }
        watch1.stop();
        System.out.println(watch1.getTime());
        executor.shutdown();*/

    }

    private double calculatePi(int N) {
        double sum = 0;
        Random random1 = new SecureRandom();

        for (int j = 0; j < N; j++) {
            double x = random1.nextDouble();
            double y = random1.nextDouble();
            if (x * x + y * y < 1)
                sum++;
        }
        return (sum / N);
    }
}
