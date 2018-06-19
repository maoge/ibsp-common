package ibsp.common.nio.service.impl;

import java.net.InetSocketAddress;
import java.util.Set;

/**
 * 重连任务
 */

public final class ReconnectTask {
	private Throwable lastException;
	private final InetSocketAddress remoteAddress;
	private volatile boolean done;
	private final Set<String> groupSet;

	public ReconnectTask(Set<String> groupSet, InetSocketAddress remoteAddress) {
		super();
		this.groupSet = groupSet;
		this.remoteAddress = remoteAddress;
	}

	public Set<String> getGroupSet() {
		return this.groupSet;
	}

	public Throwable getLastException() {
		return this.lastException;
	}

	public void setLastException(Throwable lastException) {
		this.lastException = lastException;
	}

	public boolean isDone() {
		return this.done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public InetSocketAddress getRemoteAddress() {
		return this.remoteAddress;
	}

}