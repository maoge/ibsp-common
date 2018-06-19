package ibsp.common.nio.service;

import java.net.InetSocketAddress;
import java.net.URI;

import ibsp.common.nio.service.config.ServerConfig;

/**
 * Remoting服务器
 */

public interface RemotingServer extends RemotingController {

	/**
	 * 设置服务器配置，包括端口、TCP选项等
	 * 
	 * @param serverConfig
	 */
	public void setServerConfig(ServerConfig serverConfig);

	/**
	 * 返回可供连接的URI
	 * 
	 * @return
	 */
	public URI getConnectURI();

	/**
	 * 返回绑定地址
	 * 
	 * @return
	 */
	public InetSocketAddress getInetSocketAddress();

}