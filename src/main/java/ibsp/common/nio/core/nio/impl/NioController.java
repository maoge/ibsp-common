package ibsp.common.nio.core.nio.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ibsp.common.nio.core.config.Configuration;
import ibsp.common.nio.core.core.CodecFactory;
import ibsp.common.nio.core.core.Handler;
import ibsp.common.nio.core.core.Session;
import ibsp.common.nio.core.core.WriteMessage;
import ibsp.common.nio.core.core.impl.AbstractController;
import ibsp.common.nio.core.nio.NioSessionConfig;
import ibsp.common.nio.core.nio.SelectionKeyHandler;
import ibsp.common.nio.core.util.SystemUtils;

public abstract class NioController extends AbstractController implements SelectionKeyHandler {

	private static Logger logger = LoggerFactory.getLogger(NioController.class);

	protected SelectorManager selectorManager;

	/**
	 * 默认selectorPoolSize
	 */
	protected int selectorPoolSize = SystemUtils.getSystemThreadCount();

	/**
	 * @see setSelectorPoolSize
	 * @return
	 */
	public int getSelectorPoolSize() {
		return this.selectorPoolSize;
	}

	/**
	 * 设置Selector池大小
	 * 
	 * @param selectorPoolSize
	 */
	public void setSelectorPoolSize(final int selectorPoolSize) {
		if (this.isStarted()) {
			throw new IllegalStateException("Controller has been started");
		}
		this.selectorPoolSize = selectorPoolSize;
	}

	public NioController() {
		super();
	}

	public NioController(final Configuration configuration, final CodecFactory codecFactory) {
		super(configuration, codecFactory);
	}

	public NioController(final Configuration configuration, final Handler handler, final CodecFactory codecFactory) {
		super(configuration, handler, codecFactory);
	}

	public NioController(final Configuration configuration) {
		super(configuration);
	}

	/**
	 * Write任务
	 * 
	 * 
	 * 
	 * @author boyan
	 * 
	 * @since 1.0, 2009-12-24 下午01:04:26
	 */
	private final class WriteTask implements Runnable {
		private final SelectionKey key;

		private WriteTask(final SelectionKey key) {
			this.key = key;
		}

		public final void run() {
			NioController.this.dispatchWriteEvent(this.key);
		}
	}

	/**
	 * Read任务
	 * 
	 * 
	 * 
	 * @author boyan
	 * 
	 * @since 1.0, 2009-12-24 下午01:04:19
	 */
	private final class ReadTask implements Runnable {
		private final SelectionKey key;

		private ReadTask(final SelectionKey key) {
			this.key = key;
		}

		public final void run() {
			NioController.this.dispatchReadEvent(this.key);
		}
	}

	/**
	 * 获取SelectorManager
	 * 
	 * @return
	 */
	public final SelectorManager getSelectorManager() {
		return this.selectorManager;
	}

	@Override
	protected void start0() throws IOException {
		try {
			this.initialSelectorManager();
			this.doStart();
		} catch (final IOException e) {
			logger.error("Start server error:{}", e.getMessage());
			this.notifyException(e);
			this.stop();
			throw e;
		}

	}

	public void setSelectorManager(final SelectorManager selectorManager) {
		this.selectorManager = selectorManager;
	}

	/**
	 * 初始化SelectorManager
	 * 
	 * @throws IOException
	 */
	protected void initialSelectorManager() throws IOException {
		if (this.selectorManager == null) {
			this.selectorManager = new SelectorManager(this.selectorPoolSize, this, this.configuration);
			this.selectorManager.start();
		}
	}

	/**
	 * Inner startup
	 * 
	 * @throws IOException
	 */
	protected abstract void doStart() throws IOException;

	/**
	 * READBLE事件派发
	 */
	public void onRead(final SelectionKey key) {
		if (this.readEventDispatcher == null) {
			this.dispatchReadEvent(key);
		} else {
			this.readEventDispatcher.dispatch(new ReadTask(key));
		}
	}

	/**
	 * 处理超时事件
	 * 
	 * @param timerRef
	 */
	public void onTimeout(final TimerRef timerRef) {
		if (!timerRef.isCanceled()) {
			if (this.readEventDispatcher == null) {
				timerRef.getRunnable().run();
			} else {
				this.readEventDispatcher.dispatch(timerRef.getRunnable());
			}
		}
	}

	/**
	 * WRITEABLE事件派发
	 */
	public void onWrite(final SelectionKey key) {
		if (this.writeEventDispatcher == null) {
			this.dispatchWriteEvent(key);
		} else {
			this.writeEventDispatcher.dispatch(new WriteTask(key));
		}
	}

	/**
	 * 关闭key对应的Channel
	 */
	public void closeSelectionKey(final SelectionKey key) {
		if (key.attachment() instanceof Session) {
			final AbstractNioSession session = (AbstractNioSession) key.attachment();
			if (session != null) {
				session.close0();
			}
		}
	}

	/**
	 * Dispatch read event
	 * 
	 * @param key
	 * @return
	 */
	protected abstract void dispatchReadEvent(final SelectionKey key);

	/**
	 * Dispatch write event
	 * 
	 * @param key
	 * @return
	 */
	protected abstract void dispatchWriteEvent(final SelectionKey key);

	@Override
	protected void stop0() throws IOException {
		if (this.selectorManager == null || !this.selectorManager.isStarted()) {
			return;
		}
		this.selectorManager.stop();
		this.selectorManager = null;
	}

	public synchronized void bind(final int port) throws IOException {
		if (this.isStarted()) {
			throw new IllegalStateException("Server has been bind to " + this.getLocalSocketAddress());
		}
		this.bind(new InetSocketAddress(port));
	}

	/**
	 * Build nio session config
	 * 
	 * @param sc
	 * @param queue
	 * @return
	 */
	protected final NioSessionConfig buildSessionConfig(final SelectableChannel sc, final Queue<WriteMessage> queue) {
		final NioSessionConfig sessionConfig = new NioSessionConfig(sc, this.getHandler(), this.selectorManager, this.getCodecFactory(),
				this.getStatistics(), queue, this.dispatchMessageDispatcher, this.isHandleReadWriteConcurrently(), this.sessionTimeout,
				this.configuration.getSessionIdleTimeout());
		return sessionConfig;
	}

}