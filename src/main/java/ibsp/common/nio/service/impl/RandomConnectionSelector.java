package ibsp.common.nio.service.impl;

import java.util.List;
import java.util.Random;

import ibsp.common.nio.core.command.RequestCommand;
import ibsp.common.nio.service.Connection;
import ibsp.common.nio.service.ConnectionSelector;
import ibsp.common.nio.service.exception.NotifyRemotingException;

/**
 * 连接选择器随机策略
 */

public class RandomConnectionSelector implements ConnectionSelector {

	/**
	 * 最大重试次数
	 */
	private static final int MAX_TIMES = 5;
	private final Random rand = new Random();

	/**
	 * 这里的connectionList未做拷贝保护是基于性能考虑，如果select失败，也是抛出Runtime异常
	 */
	public final Connection select(final String targetGroup, final RequestCommand request, final List<Connection> connectionList)
			throws NotifyRemotingException {
		try {
			if (connectionList == null) {
				return null;
			}
			final int size = connectionList.size();
			if (size == 0) {
				return null;
			}
			Connection result = connectionList.get(this.rand.nextInt(size));
			int tries = 0;
			while ((result == null || !result.isConnected()) && tries++ < MAX_TIMES) {
				result = connectionList.get(this.rand.nextInt(size));
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