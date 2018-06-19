package ibsp.common.nio.core.core;

import java.util.Queue;

import ibsp.common.nio.core.statistics.Statistics;

/**
 * 连接配置
 */
public class SessionConfig {
	public final Handler handler;
	public final CodecFactory codecFactory;
	public final Statistics statistics;
	public final Queue<WriteMessage> queue;
	public final Dispatcher dispatchMessageDispatcher;
	public final boolean handleReadWriteConcurrently;
	public final long sessionTimeout;
	public final long sessionIdelTimeout;

	public SessionConfig(final Handler handler, final CodecFactory codecFactory, final Statistics statistics,
			final Queue<WriteMessage> queue, final Dispatcher dispatchMessageDispatcher, final boolean handleReadWriteConcurrently,
			final long sessionTimeout, final long sessionIdelTimeout) {

		this.handler = handler;
		this.codecFactory = codecFactory;
		this.statistics = statistics;
		this.queue = queue;
		this.dispatchMessageDispatcher = dispatchMessageDispatcher;
		this.handleReadWriteConcurrently = handleReadWriteConcurrently;
		this.sessionTimeout = sessionTimeout;
		this.sessionIdelTimeout = sessionIdelTimeout;
	}
}