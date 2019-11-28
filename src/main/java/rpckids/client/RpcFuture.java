package rpckids.client;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RpcFuture<T> implements Future<T> {

	private T result;
	private Throwable error;
	private CountDownLatch latch = new CountDownLatch(1);

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
		return result != null || error != null;
	}

	public void success(T result) {
		this.result = result;
		// messageCollecter 收取到result将其放开
		latch.countDown();
	}
    // 设置错误，并放开锁
	public void fail(Throwable error) {
		this.error = error;
		latch.countDown();
	}

	@Override
	public T get() throws InterruptedException, ExecutionException {
		// rpc future里面使用countdownLatch来让用户获取result时阻塞
		latch.await();
		if (error != null) {
			throw new ExecutionException(error);
		}
		return result;
	}

	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		latch.await(timeout, unit);
		if (error != null) {
			throw new ExecutionException(error);
		}
		return result;
	}

}
