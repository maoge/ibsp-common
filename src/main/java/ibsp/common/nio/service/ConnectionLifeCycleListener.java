package ibsp.common.nio.service;

/**
 * 连接生命周期监听器
 */

public interface ConnectionLifeCycleListener {
	/**
	 * 当连接建立时回调，还未加入所在分组
	 * 
	 * @param conn
	 */
	public void onConnectionCreated(Connection conn);

	/**
	 * 连接就绪，已经加入所在分组，只对客户端有意义
	 * 
	 * @param conn
	 */
	public void onConnectionReady(Connection conn);

	/**
	 * 当连接关闭时回调
	 * 
	 * @param conn
	 */
	public void onConnectionClosed(Connection conn);
}