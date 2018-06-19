package ibsp.common.nio.service.impl;

import java.util.List;

import ibsp.common.nio.core.command.RequestCommand;
import ibsp.common.nio.core.util.PositiveAtomicCounter;
import ibsp.common.nio.service.Connection;
import ibsp.common.nio.service.ConnectionSelector;
import ibsp.common.nio.service.exception.NotifyRemotingException;

/**
 * Round robin的连接选择器
 */
public class RoundRobinConnectionSelector implements ConnectionSelector {
	private final PositiveAtomicCounter sets = new PositiveAtomicCounter();

	private static int MAX_TIMES = 5;

	public Connection select(final String targetGroup, final RequestCommand request, final List<Connection> connectionList)
			throws NotifyRemotingException {
		try {
			if (connectionList == null) {
				return null;
			}
			final int size = connectionList.size();
			if (size == 0) {
				return null;
			}
			Connection result = connectionList.get(this.sets.incrementAndGet() % size);
			int tries = 0;
			while ((result == null || !result.isConnected()) && tries++ < MAX_TIMES) {
				result = connectionList.get(this.sets.incrementAndGet() % size);
			}
			if (result != null && !result.isConnected()) {
				return null;
			}
			return result;
		} catch (final Throwable e) {
			throw new NotifyRemotingException(e);
		}
	}

}