package photon.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Accepts pi estimates up to <code>limit</code> times. Then prints the average of all estimates. <br>
 * If a <code>Stopwatch</code> is provided when creating a <code>PiEstimateAggregator</code>, the watch will be stopped
 * when enough estimates are collected and the elapsed time will also be printed.
 */
public class PiEstimateAggregator {
    private final int limit;
    private final Stopwatch stopwatch;

    private final Lock lock = new ReentrantLock();
    private int _count = 0;
    private double _adder = 0;

    public PiEstimateAggregator(int limit, Stopwatch stopwatch) {
        this.limit = limit;
        this.stopwatch = stopwatch;
    }

    public void reset() {
        _count = 0;
        _adder = 0;
    }

    public void add(double x) {
        lock.lock();
        _count++;
        _adder += x;
        if (_count == limit) {
            if (stopwatch != null) {
                stopwatch.stop();
                System.out.println("Elapsed time (milliseconds): " + stopwatch.millis());
            }
            System.out.println("Estimated Pi is " + 4 * _adder / limit);
            System.out.println("---------------------  End test  ---------------------");
        }
        lock.unlock();
    }
}
