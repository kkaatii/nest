package photon.tube.query;

import photon.tube.action.Callback;

import java.util.concurrent.*;

public class QueryFuture implements Future<QueryResult>, Callback<QueryResult> {

    private final CountDownLatch latch = new CountDownLatch(1);
    private QueryResult result;

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return latch.getCount() == 0;
    }

    @Override
    public QueryResult get() throws InterruptedException, ExecutionException {
        latch.await();
        return result;
    }

    @Override
    public QueryResult get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        latch.await(timeout, unit);
        return result;
    }

    @Override
    public void onSuccess(QueryResult input) {
        result = input;
        latch.countDown();
    }

    @Override
    public void onFailure() {

    }
}
