package ibsp.common.nio.service;

import java.io.IOException;
import java.net.InetSocketAddress;

import ibsp.common.nio.service.config.ClientConfig;
import ibsp.common.nio.service.exception.NotifyRemotingException;

/**
 * Notify Remoting的客户端接口
 */

public interface RemotingClient extends RemotingController {
	/**
	 * 根据URL连接服务端，如果连接失败将转入重连模式
	 * 
	 * @param group
	 *            服务端的URL，形如schema://host:port的字符串
	 * @throws IOException
	 */
	public void connect(String url) throws NotifyRemotingException;

	/**
	 * 等待连接就绪，可中断，连接就绪的含义如下：是指指定分组的有效连接数达到设定值，并且可用。默认等待超时为连接数乘以连接超时
	 * 
	 * @param group
	 * @throws NotifyRemotingException
	 * @throws InterruptedException
	 */
	public void awaitReadyInterrupt(String url) throws NotifyRemotingException, InterruptedException;

	/**
	 * 等待连接就绪，可中断，连接就绪的含义如下：是指指定分组的有效连接数达到设定值，并且可用。默认等待超时为连接数乘以连接超时
	 * 
	 * @param group
	 * @throws NotifyRemotingException
	 * @throws InterruptedException
	 */
	public void awaitReadyInterrupt(String url, long time) throws NotifyRemotingException, InterruptedException;

	/**
	 * 根据URL连接服务端，如果连接失败将转入重连模式
	 * 
	 * @param url
	 *            服务端的URL，形如schema://host:port的字符串
	 * @throws IOException
	 */
	public void connect(String url, int connCount) throws NotifyRemotingException;

	/**
	 * 获取远端地址
	 * 
	 * @param url
	 *            服务端的url，形如schema://host:port的字符串
	 * @return
	 */
	public InetSocketAddress getRemoteAddress(String url);

	/**
	 * 获取远端地址
	 * 
	 * @param url
	 *            服务端的group，形如schema://host:port的字符串
	 * @return
	 */
	public String getRemoteAddressString(String url);

	/**
	 * 判断url对应的连接是否可用，注意，如果设置了连接池，那么如果连接池中任一连接可用，即认为可用
	 * 
	 * @param url
	 *            服务端的url，形如schema://host:port的字符串
	 * @return
	 */
	public boolean isConnected(String url);

	/**
	 * 关闭url对应的连接
	 * 
	 * @param url
	 *            服务端的url，形如schema:://host:port的字符串
	 * @param allowReconnect
	 *            是否需要重连
	 * @throws NotifyRemotingException
	 * 
	 */
	public void close(String url, boolean allowReconnect) throws NotifyRemotingException;

	/**
	 * 设置客户端配置，只能在启动前设置，启动后设置无效
	 * 
	 * @param clientConfig
	 */
	public void setClientConfig(ClientConfig clientConfig);

}