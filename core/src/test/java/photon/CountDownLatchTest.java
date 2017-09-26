package photon;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Obviously, the <code>Thread</code> calling <code>CountDownLatch.await()</code> will not release its position
 * in the thread pool.
 */
public class CountDownLatchTest {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.submit(() -> {
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread.sleep(500);

        executor.submit(latch::countDown);

        while (latch.getCount() > 0) {

        }
    }
}
