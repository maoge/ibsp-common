package ibsp.common.nio.service.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ibsp.common.nio.core.command.Constants;
import ibsp.common.nio.core.config.Configuration;
import ibsp.common.nio.core.extension.ConnectFailListener;
import ibsp.common.nio.core.extension.GeckoTCPConnectorController;
import ibsp.common.nio.core.nio.NioSession;
import ibsp.common.nio.core.nio.impl.SocketChannelController;
import ibsp.common.nio.core.nio.impl.TimerRef;
import ibsp.common.nio.core.util.RemotingUtils;
import ibsp.common.nio.core.util.StringUtils;
import ibsp.common.nio.service.Connection;
import ibsp.common.nio.service.RemotingClient;
import ibsp.common.nio.service.config.ClientConfig;
import ibsp.common.nio.service.exception.NotifyRemotingException;

/**
 * RemotingClient的默认实现
 */

public class DefaultRemotingClient extends BaseRemotingController implements RemotingClient, ConnectFailListener {

	private static Logger logger = LoggerFactory.getLogger(DefaultRemotingClient.class);

	private ReconnectManager reconnectManager;

	public DefaultRemotingClient(final ClientConfig clientConfig) {
		super(clientConfig);
		this.config = clientConfig;
		// 默认分组的最大连接数设置为Integer.MAX_VALUE
		this.setAttribute(Constants.DEFAULT_GROUP, Constants.CONNECTION_COUNT_ATTR, Integer.MAX_VALUE);

	}

	public void close(final String group, final boolean allowReconnect) throws NotifyRemotingException {
		if (!this.started) {
			throw new NotifyRemotingException("The controller has been stopped");
		}
		if (group == null) {
			throw new IllegalArgumentException("null group");
		}
		if (!allowReconnect) {
			// 取消重连任务
			this.reconnectManager.cancelReconnectGroup(group);
			// 删除属性
			this.attributes.remove(group);
		}
		final List<Connection> connections = this.remotingContext.getConnectionsByGroup(group);
		if (connections != null) {
			for (final Connection conn : connections) {
				if (conn.isConnected()) {
					conn.close(allowReconnect);
				}
			}
		}

	}

	/**
	 * 这里需要同步，防止对同一个分组发起多个请求
	 */
	public synchronized void connect(String group, final int connCount) throws NotifyRemotingException {
		if (connCount <= 0) {
			throw new IllegalArgumentException("非法连接数，必须大于0");
		}
		group = group.trim();
		if (this.isGroupConnectPending(group)) {
			return;
		}

		final InetSocketAddress remoteAddress = this.getSocketAddrFromGroup(group);

		final Set<String> groupSet = new HashSet<String>();
		groupSet.add(group);
		this.reconnectManager.removeCanceledGroup(group);
		// 设置连接数属性
		if (this.setAttributeIfAbsent(group, Constants.CONNECTION_COUNT_ATTR, connCount) != null) {
			return;
		}
		// 设置连接就绪锁
		if (this.setAttributeIfAbsent(group, Constants.GROUP_CONNECTION_READY_LOCK, new Object()) != null) {
			return;
		}
		for (int i = 0; i < connCount; i++) {
			try {
				final TimerRef timerRef = new TimerRef(((ClientConfig) this.config).getConnectTimeout(), null);
				final Future<NioSession> future = ((GeckoTCPConnectorController) this.controller).connect(remoteAddress, groupSet,
						remoteAddress, timerRef);
				final CheckConnectFutureRunner runnable = new CheckConnectFutureRunner(future, remoteAddress, groupSet, this);
				timerRef.setRunnable(runnable);
				this.insertTimer(timerRef);
			} catch (final Exception e) {
				logger.error("连接{}失败,启动重连任务, errmsg:{}", RemotingUtils.getAddrString(remoteAddress), e.getMessage());
				this.reconnectManager.addReconnectTask(new ReconnectTask(groupSet, remoteAddress));
			}
		}
	}

	/**
	 * 判断分组是否发起过连接请求
	 * 
	 * @param group
	 * @return
	 */
	private boolean isGroupConnectPending(final String group) {
		final Object readyLock = this.getAttribute(group, Constants.GROUP_CONNECTION_READY_LOCK);
		final Object attribute = this.getAttribute(group, Constants.CONNECTION_COUNT_ATTR);
		return readyLock != null && attribute != null;
	}

	public ReconnectManager getReconnectManager() {
		return this.reconnectManager;
	}

	/**
	 * 检测连接建立是否成功
	 * 
	 * 
	 * 
	 * @author boyan
	 * 
	 * @since 1.0, 2009-12-23 下午01:49:41
	 */
	public static final class CheckConnectFutureRunner implements Runnable {
		final Future<NioSession> future;
		final InetSocketAddress remoteAddress;
		final Set<String> groupSet;
		final DefaultRemotingClient remotingClient;

		public CheckConnectFutureRunner(final Future<NioSession> future, final InetSocketAddress remoteAddress, final Set<String> groupSet,
				final DefaultRemotingClient remotingClient) {
			super();
			this.future = future;
			this.remoteAddress = remoteAddress;
			this.groupSet = groupSet;
			this.remotingClient = remotingClient;
		}

		public void run() {
			try {
				if (!this.future.isDone() && this.future.get(10, TimeUnit.MILLISECONDS) == null) {
					final ReconnectManager reconnectManager = this.remotingClient.getReconnectManager();
					reconnectManager.addReconnectTask(new ReconnectTask(this.groupSet, this.remoteAddress));
				}
			} catch (final Exception e) {
				logger.error("连接{}失败, errmsg:{}", this.remoteAddress, e.getMessage());
			}
		}

	}

	private InetSocketAddress getSocketAddrFromGroup(String group) throws NotifyRemotingException {
		if (group == null) {
			throw new IllegalArgumentException("Null group");
		}
		group = group.trim();
		if (!group.startsWith(this.config.getWireFormatType().getScheme())) {
			throw new NotifyRemotingException("非法的Group格式，没有以" + this.config.getWireFormatType().getScheme() + "开头");
		}
		try {
			final URI uri = new URI(group);
			return new InetSocketAddress(uri.getHost(), uri.getPort());
		} catch (final Exception e) {
			throw new NotifyRemotingException("从uri生成服务器地址出错,url=" + group, e);
		}
	}

	public void connect(final String group) throws NotifyRemotingException {
		this.connect(group, 1);

	}

	public void awaitReadyInterrupt(final String group) throws NotifyRemotingException, InterruptedException {
		final Object readyLock = this.getAttribute(group, Constants.GROUP_CONNECTION_READY_LOCK);
		final Object attribute = this.getAttribute(group, Constants.CONNECTION_COUNT_ATTR);
		if (readyLock == null || attribute == null) {
			throw new IllegalStateException("非法状态，你还没有调用connect方法进行连接操作。");
		}
		final long defaultConnectTimeout = ((ClientConfig) this.config).getConnectTimeout();
		this.awaitReadyInterrupt(group, defaultConnectTimeout * (Integer) attribute);
	}

	public void awaitReadyInterrupt(final String group, final long time) throws NotifyRemotingException, InterruptedException {
		if (StringUtils.isBlank(group)) {
			throw new IllegalArgumentException("Blank group");
		}
		// 获取分组连接就绪锁
		final Object readyLock = this.getAttribute(group, Constants.GROUP_CONNECTION_READY_LOCK);
		final Object attribute = this.getAttribute(group, Constants.CONNECTION_COUNT_ATTR);
		if (readyLock == null || attribute == null) {
			throw new IllegalStateException("非法状态，你还没有调用connect方法进行连接操作。");
		} else {
			final int maxConnCount = (Integer) attribute;
			long totalTime = 0;
			synchronized (readyLock) {
				while (this.getConnectionCount(group) != maxConnCount) {
					final long start = System.currentTimeMillis();
					readyLock.wait(1000);
					totalTime += System.currentTimeMillis() - start;
					if (totalTime >= time) {
						throw new NotifyRemotingException("等待连接就绪超时，超时时间为" + time + "毫秒");
					}
				}
			}
		}

	}

	public InetSocketAddress getRemoteAddress(final String group) {
		if (this.remotingContext == null) {
			return null;
		}
		final List<Connection> connections = this.remotingContext.getConnectionsByGroup(group);
		if (connections == null || connections.size() == 0) {
			return null;
		}
		for (final Connection conn : connections) {
			if (conn.getRemoteSocketAddress() != null) {
				return conn.getRemoteSocketAddress();
			}
		}
		return null;
	}

	public String getRemoteAddressString(final String group) {
		return RemotingUtils.getAddrString(this.getRemoteAddress(group));
	}

	public boolean isConnected(final String group) {
		if (this.remotingContext == null) {
			return false;
		}
		final List<Connection> connections = this.remotingContext.getConnectionsByGroup(group);
		if (connections == null || connections.size() == 0) {
			return false;
		}
		for (final Connection conn : connections) {
			if (conn.isConnected()) {
				return true;
			}
		}
		return false;
	}

	public void setClientConfig(final ClientConfig clientConfig) {
		if (this.controller != null && this.controller.isStarted()) {
			throw new IllegalStateException("RemotingClient已经启动，设置无效");
		}
		this.config = clientConfig;
	}

	@Override
	protected void doStart() throws NotifyRemotingException {
		this.startController();
		this.startReconnectManager();
	}

	private void startReconnectManager() {
		// 启动重连管理器
		this.reconnectManager = new ReconnectManager((GeckoTCPConnectorController) this.controller, (ClientConfig) this.config, this);
		((GeckoHandler) this.controller.getHandler()).setReconnectManager(this.reconnectManager);
		this.reconnectManager.start();
	}

	private void startController() throws NotifyRemotingException {
		try {
			this.controller.start();
		} catch (final IOException e) {
			throw new NotifyRemotingException("启动控制器出错", e);
		}
	}

	@Override
	protected void doStop() throws NotifyRemotingException {
		this.stopReconnectManager();
		this.closeAllConnection();
	}

	private void closeAllConnection() throws NotifyRemotingException {
		// 关闭所有连接
		final List<Connection> connections = this.remotingContext.getConnectionsByGroup(Constants.DEFAULT_GROUP);
		if (connections != null) {
			for (final Connection conn : connections) {
				((DefaultConnection) conn).setReady(true);// 强制为就绪状态
				conn.close(false);
			}
		}
	}

	private void stopReconnectManager() {
		this.reconnectManager.stop();
	}

	/**
	 * 当连接失败的时候回调
	 */
	@SuppressWarnings("unchecked")
	public void onConnectFail(final Object... args) {
		if (args.length >= 2) {
			final Set<String> groupSet = (Set<String>) args[0];
			final InetSocketAddress remoteAddr = (InetSocketAddress) args[1];
			this.reconnectManager.addReconnectTask(new ReconnectTask(groupSet, remoteAddr));
			if (args.length >= 3) {
				final TimerRef timerRef = (TimerRef) args[2];
				timerRef.cancel();
			}
		}

	}

	@Override
	protected SocketChannelController initController(final Configuration conf) {
		final GeckoTCPConnectorController notifyTCPConnectorController = new GeckoTCPConnectorController(conf);
		// 设置连接失败监听器
		notifyTCPConnectorController.setConnectFailListener(this);
		return notifyTCPConnectorController;
	}

}