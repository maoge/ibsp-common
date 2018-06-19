package ibsp.common.nio.core.nio;

import java.nio.channels.SelectableChannel;
import java.util.Queue;

import ibsp.common.nio.core.core.CodecFactory;
import ibsp.common.nio.core.core.Dispatcher;
import ibsp.common.nio.core.core.Handler;
import ibsp.common.nio.core.core.SessionConfig;
import ibsp.common.nio.core.core.WriteMessage;
import ibsp.common.nio.core.nio.impl.SelectorManager;
import ibsp.common.nio.core.statistics.Statistics;

/**
 * Nio session配置
 */
public class NioSessionConfig extends SessionConfig {

	public final SelectableChannel selectableChannel;
	public final SelectorManager selectorManager;

	public NioSessionConfig(final SelectableChannel sc, final Handler handler, final SelectorManager reactor,
			final CodecFactory codecFactory, final Statistics statistics, final Queue<WriteMessage> queue,
			final Dispatcher dispatchMessageDispatcher, final boolean handleReadWriteConcurrently, final long sessionTimeout,
			final long sessionIdleTimeout) {
		super(handler, codecFactory, statistics, queue, dispatchMessageDispatcher, handleReadWriteConcurrently, sessionTimeout,
				sessionIdleTimeout);
		this.selectableChannel = sc;
		this.selectorManager = reactor;
	}

}