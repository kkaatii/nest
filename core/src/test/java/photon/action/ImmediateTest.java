package photon.action;

import photon.util.Stopwatch;

public class ImmediateTest {
    public static void main(String[] args) throws InterruptedException {
        ActionScheduler scheduler = new ActionScheduler(4);
        Stopwatch stopwatch = new Stopwatch();
        Action immediate1 = new Action() {
            @Override
            void run() {
                System.out.println("1 is run immediately at " + stopwatch.stamp());
            }
        }.setImmediate(true);
        Action immediate2 = new Action() {
            @Override
            void run() {
                System.out.println("2 is run immediately at " + stopwatch.stamp());
            }
        }.setImmediate(true);
        Action immediate3 = new Action() {
            @Override
            void run() {
                System.out.println("3 is run immediately at " + stopwatch.stamp());
            }
        }.setImmediate(true);
        Action immediate4 = new Action() {
            @Override
            void run() {
                System.out.println("4 is run immediately at " + stopwatch.stamp());
            }
        }.setImmediate(true);
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
        scheduler.submit(immediate1.then(scheduled1));
        immediate2.then(immediate4).then(immediate3).then(scheduled4);
        scheduler.submit(immediate2);
//        System.out.println(immediate3.state());
        scheduler.submit(immediate3);
//        Thread.sleep(200);
//        System.out.println(immediate3.state());
//        Thread.sleep(2500);
//        System.out.println(immediate3.state());
//
//        Thread.sleep(10000);
//        scheduler.submit(immediate2);
        scheduler.shutdown();

    }
}
