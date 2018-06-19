package ibsp.common.nio.service;

import ibsp.common.nio.service.config.ClientConfig;
import ibsp.common.nio.service.config.ServerConfig;
import ibsp.common.nio.service.exception.NotifyRemotingException;
import ibsp.common.nio.service.impl.DefaultRemotingClient;
import ibsp.common.nio.service.impl.DefaultRemotingServer;

/**
 * Remoting工厂，创建通讯层组件
 */

public final class RemotingFactory {

	/**
	 * 初始化并启动服务器，绑定到指定IP地址
	 * 
	 * @param serverConfig
	 * @return
	 * @throws NotifyRemotingException
	 */
	public static RemotingServer bind(final ServerConfig serverConfig) throws NotifyRemotingException {
		final RemotingServer server = newRemotingServer(serverConfig);
		server.start();
		return server;
	}

	/**
	 * 创建一个服务器对象，不启动
	 * 
	 * @param serverConfig
	 * @return
	 */
	public static RemotingServer newRemotingServer(final ServerConfig serverConfig) {
		return new DefaultRemotingServer(serverConfig);
	}

	/**
	 * 创建一个客户端对象，不启动
	 * 
	 * @param clientConfig
	 * @return
	 */
	public static RemotingClient newRemotingClient(final ClientConfig clientConfig) {
		return new DefaultRemotingClient(clientConfig);
	}

	/**
	 * 创建一个客户端对象并启动
	 * 
	 * @param clientConfig
	 * @return
	 * @throws NotifyRemotingException
	 */
	public static RemotingClient connect(final ClientConfig clientConfig) throws NotifyRemotingException {
		final DefaultRemotingClient remotingClient = new DefaultRemotingClient(clientConfig);
		remotingClient.start();
		return remotingClient;
	}
}