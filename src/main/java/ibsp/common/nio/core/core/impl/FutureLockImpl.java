package ibsp.common.nio.core.core.impl;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Simple {@link Future} implementation, which uses {@link ReentrantLock} to
 * synchronize during the lifecycle.
 * 
 * @see Future
 * @see ReentrantLock
 */
public class FutureLockImpl<R> implements Future<R> {

	private final ReentrantLock lock;

	private boolean isDone;

	private CountDownLatch latch;

	private boolean isCancelled;
	private Throwable failure;

	protected R result;

	public FutureLockImpl() {
		this(new ReentrantLock());
	}

	public FutureLockImpl(ReentrantLock lock) {
		this.lock = lock;
		latch = new CountDownLatch(1);
	}

	/**
	 * Get current result value without any blocking.
	 * 
	 * @return current result value without any blocking.
	 */
	public R getResult() {
		try {
			lock.lock();
			return result;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Set the result value and notify about operation completion.
	 * 
	 * @param result
	 *            the result value
	 */
	public void setResult(R result) {
		try {
			lock.lock();
			this.result = result;
			notifyHaveResult();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean cancel(boolean mayInterruptIfRunning) {
		try {
			lock.lock();
			isCancelled = true;
			notifyHaveResult();
			return true;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isCancelled() {
		try {
			lock.lock();
			return isCancelled;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isDone() {
		try {
			lock.lock();
			return isDone;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public R get() throws InterruptedException, ExecutionException {
		latch.await();

		try {
			lock.lock();
			if (isCancelled) {
				throw new CancellationException();
			} else if (failure != null) {
				throw new ExecutionException(failure);
			}

			return result;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public R get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		boolean isTimeOut = !latch.await(timeout, unit);
		try {
			lock.lock();
			if (!isTimeOut) {
				if (isCancelled) {
					throw new CancellationException();
				} else if (failure != null) {
					throw new ExecutionException(failure);
				}

				return result;
			} else {
				throw new TimeoutException();
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Notify about the failure, occured during asynchronous operation
	 * execution.
	 * 
	 * @param failure
	 */
	public void failure(Throwable failure) {
		try {
			lock.lock();
			this.failure = failure;
			notifyHaveResult();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Notify blocked listeners threads about operation completion.
	 */
	protected void notifyHaveResult() {
		isDone = true;
		latch.countDown();
	}
}