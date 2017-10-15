package photon.util;

/**
 * Simplified implementation of a stopwatch.
 */
public class Stopwatch {

    private long startMillis = 0;
    private long stopMillis = 0;

    public void start() {
        startMillis = System.currentTimeMillis();
    }

    public void stop() {
        stopMillis = System.currentTimeMillis();
    }

    public long millis() {
        return stopMillis - startMillis;
    }

    public long seconds() {
        return (stopMillis - startMillis) / 1000;
    }

    public double stamp() {
        long now = System.currentTimeMillis();
        return (now - startMillis) / 1000.0;
    }

}
