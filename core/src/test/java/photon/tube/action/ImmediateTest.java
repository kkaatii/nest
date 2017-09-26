package photon.tube.action;

import photon.util.Stopwatch;

/**
 * Created by dan on 25/09/2017.
 */
public class ImmediateTest {
    public static void main(String[] args) {
        ActionManager manager = new ActionManager();
        Stopwatch stopwatch = new Stopwatch();
        Action immediate1 = new Action() {
            @Override
            void run() {
                System.out.println("1 is run immediately at " + stopwatch.stamp());
            }
            @Override
            public boolean isImmediate() {
                return true;
            }
        };
        Action immediate2 = new Action() {
            @Override
            void run() {
                System.out.println("2 is run immediately at " + stopwatch.stamp());
            }
            @Override
            public boolean isImmediate() {
                return true;
            }
        };
        Action immediate3 = new Action() {
            @Override
            void run() {
                System.out.println("3 is run immediately at " + stopwatch.stamp());
            }
            @Override
            public boolean isImmediate() {
                return true;
            }
        };
        Action immediate4 = new Action() {
            @Override
            void run() {
                System.out.println("4 is run immediately at " + stopwatch.stamp());
            }
            @Override
            public boolean isImmediate() {
                return true;
            }
        };
        Action scheduled1 = new Action() {
            @Override
            void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("S1 is waken up after " + stopwatch.stamp());
            }
        };
        Action scheduled2 = new Action() {
            @Override
            void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("S2 is waken up after " + stopwatch.stamp());
            }
        };
        Action scheduled3 = new Action() {
            @Override
            void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("S3 is waken up after " + stopwatch.stamp());
            }
        };
        Action scheduled4 = new Action() {
            @Override
            void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("S4 is waken up after " + stopwatch.stamp());
            }
        };
        stopwatch.start();
        manager.schedule(immediate1.then(scheduled1));
        manager.schedule(immediate2.then(scheduled2).then(immediate3).then(scheduled4));
        manager.schedule(scheduled3);

    }
}
