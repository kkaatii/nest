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

    static class NumProvider extends BeginAction<Integer> {
        private int n;

        public NumProvider(int n) {
            this.n = n;
        }

        @Override
        public Integer transform(Void _void) {
            return n;
        }
    }

    static class PiCalculator extends Transformation<Void, Double> {
        private final int n;

        public PiCalculator(int n) {
            this.n = n;
            setPerformStrategy(PerformStrategy.FORCE_UPDATE);
        }

        @Override
        public Double transform(Void _void) {
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

    }

    static class PredecessorInspector extends Transformation<Void, Void> {
        protected PredecessorInspector() {

        }

        @Override
        public void abort(ActionRuntimeException e) {
            super.abort(e);
            Action prev = predecessor;
            while (prev != null) {
                System.out.println(prev.state);
                prev = prev.predecessor;
            }
        }

        @Override
        protected Void transform(Void input) {
            return null;
        }

        @Override
        public final boolean isImmediate() {
            return true;
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
        int times = 6;
        List<BeginAction<Void>> numList = new ArrayList<>();

        StopWatch watch = new StopWatch();
        Average average = new Average(0, times, watch);

        for (int i = 0; i < times; i++) {

            Transformation<Void, Double> piCalc = new PiCalculator(N);
            CallbackAction<Double> write = new CallbackAction<>(new Callback<Double>() {
                @Override
                public void onSuccess(Double value) {
                    average.addValue(value);
                }

                @Override
                public void onFailure() {
                }
            });
            PredecessorInspector inspect = new PredecessorInspector();
            piCalc.then(write);
            numList.add(BeginAction.wrap(piCalc));
        }

        // Warm-up JVM
        System.out.println(calculatePi(N * times));

        // Warm-up again
        for (int i = 0; i < 10; i++) {
            new Thread(() -> calculatePi(N * 10)).start();
        }
        Thread.sleep(2000L);

        // Test actions
        watch.start();
        watch.suspend();
        for (int i = 0; i < 100; i++) {
            watch.resume();
            for (BeginAction<?> actionable : numList) {
                actionable.perform();
            }
            Thread.sleep(250L);
        }
        watch.stop();
        System.out.println(watch.getTime());
        numList.get(0).manager().shutdown();

        // Test plain calculation
        /*ExecutorService executor = Executors.newFixedThreadPool(4);
        StopWatch watch1 = new StopWatch();
        Average average1 = new Average(1, times, watch1);
        Supplier<Double> supplier = new PiCalc(N);
        List<Supplier<Double>> funcList = new ArrayList<>();
        for (int i = 0; i < times; i++) {
            funcList.add(supplier);
        }
        watch1.start();
        watch1.suspend();
        for (int i = 0; i < 1000; i++) {
            watch1.resume();
            for (Supplier<Double> supplier1 : funcList) {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        average1.addValue(supplier1.get());
                    }
                });
            }
            Thread.sleep(250L);
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
