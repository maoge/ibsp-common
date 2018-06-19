package ibsp.common.nio.service.impl;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import ibsp.common.nio.core.command.Constants;
import ibsp.common.nio.core.command.kernel.HeartBeatRequestCommand;
import ibsp.common.nio.core.config.Configuration;
import ibsp.common.nio.core.nio.TCPController;
import ibsp.common.nio.core.nio.impl.SocketChannelController;
import ibsp.common.nio.core.util.RemotingUtils;
import ibsp.common.nio.service.Connection;
import ibsp.common.nio.service.RemotingServer;
import ibsp.common.nio.service.config.ServerConfig;
import ibsp.common.nio.service.exception.NotifyRemotingException;
import ibsp.common.nio.service.processor.HeartBeatCommandProecssor;

/**
 * RemotingServer，服务器的默认实现
 */

public class DefaultRemotingServer extends BaseRemotingController implements RemotingServer {

	public DefaultRemotingServer(final ServerConfig serverConfig) {
		super(serverConfig);
		this.config = serverConfig;

	}

	public void setServerConfig(final ServerConfig serverConfig) {
		if (this.controller != null && this.controller.isStarted()) {
			throw new IllegalStateException("RemotingServer已经启动，设置无效");
		}
		this.config = serverConfig;
	}

	/**
	 * 服务端还需要扫描连接是否存活
	 */
	@Override
	protected ScanTask[] getScanTasks() {
		return new ScanTask[] { new InvalidCallBackScanTask(), new InvalidConnectionScanTask() };
	}

	@Override
	protected void doStart() throws NotifyRemotingException {
		// 如果没有设置心跳处理器，则使用默认
		if (!this.remotingContext.processorMap.containsKey(HeartBeatRequestCommand.class)) {
			this.registerProcessor(HeartBeatRequestCommand.class, new HeartBeatCommandProecssor());
		}
		try {

			final ServerConfig serverConfig = (ServerConfig) this.config;
			((TCPController) this.controller).setBacklog(serverConfig.getBacklog());
			// 优先绑定指定IP地址
			if (serverConfig.getLocalInetSocketAddress() != null) {
				this.controller.bind(serverConfig.getLocalInetSocketAddress());
			} else {
				this.controller.bind(serverConfig.getPort());
			}
		} catch (final IOException e) {
			throw new NotifyRemotingException(e);
		}
	}

	@Override
	protected void doStop() throws NotifyRemotingException {
		// 关闭所有连接
		final List<Connection> connections = this.remotingContext.getConnectionsByGroup(Constants.DEFAULT_GROUP);
		if (connections != null) {
			for (final Connection conn : connections) {
				conn.close(false);
			}
		}
	}

	public synchronized URI getConnectURI() {
		final InetSocketAddress socketAddress = this.getInetSocketAddress();
		if (socketAddress == null) {
			throw new IllegalStateException("server未启动");
		}
		InetAddress inetAddress = null;
		try {
			inetAddress = RemotingUtils.getLocalHostAddress();
		} catch (final Exception e) {
			throw new IllegalStateException("获取IP地址失败", e);
		}
		try {
			if (inetAddress instanceof Inet4Address) {
				return new URI(this.config.getWireFormatType().getScheme() + "://" + inetAddress.getHostAddress() + ":"
						+ socketAddress.getPort());
			} else if (inetAddress instanceof Inet6Address) {
				return new URI(this.config.getWireFormatType().getScheme() + "://[" + inetAddress.getHostAddress() + "]:"
						+ socketAddress.getPort());
			} else {
				throw new IllegalStateException("Unknow InetAddress type " + inetAddress);
			}
		} catch (final URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}

	public InetSocketAddress getInetSocketAddress() {
		return this.controller == null ? null : this.controller.getLocalSocketAddress();
	}

	@Override
	protected SocketChannelController initController(final Configuration conf) {
		return new TCPController(conf);
	}

}