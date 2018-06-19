package ibsp.common.nio.service.udp;

import ibsp.common.nio.service.exception.NotifyRemotingException;

/**
 * UDP服务控制器
 */
public interface UDPController {
	/**
	 * 启动服务
	 * 
	 * @throws NotifyRemotingException
	 */
	public void start() throws NotifyRemotingException;

	/**
	 * 关闭服务
	 * 
	 * @throws NotifyRemotingException
	 */
	public void stop() throws NotifyRemotingException;

	/**
	 * 返回处理器
	 * 
	 * @return
	 */
	public UDPServiceHandler getUDPServiceHandler();

	public boolean isStarted();
}