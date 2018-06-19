package ibsp.common.nio.service.config;

import java.net.InetSocketAddress;

public final class ServerConfig extends BaseConfig {
	/**
	 * 端口
	 */
	private int port = 9527;
	/**
	 * backlog队列大小
	 */
	private int backlog = 1000;

	private InetSocketAddress localInetSocketAddress;

	public InetSocketAddress getLocalInetSocketAddress() {
		return this.localInetSocketAddress;
	}

	public void setLocalInetSocketAddress(InetSocketAddress localInetSocketAddress) {
		this.localInetSocketAddress = localInetSocketAddress;
	}

	public ServerConfig() {
		super();
		this.setIdleTime(-1);
	}

	public int getBacklog() {
		return this.backlog;
	}

	public void setBacklog(int backlog) {
		this.backlog = backlog;
	}

	public int getPort() {
		return this.port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}