package ibsp.common.nio.service;

import java.util.concurrent.ThreadPoolExecutor;

import ibsp.common.nio.core.command.ResponseCommand;

/**
 * 单个分组的单个连接的应答回调监听器
 */

public interface SingleRequestCallBackListener {

	/**
	 * 处理应答
	 * 
	 * @param responseCommand
	 *            应答命令
	 * @param conn
	 *            应答连接
	 */
	public void onResponse(ResponseCommand responseCommand, Connection conn);

	/**
	 * 异常发生的时候回调
	 * 
	 * @param e
	 */
	public void onException(Exception e);

	/**
	 * onResponse回调执行的线程池
	 * 
	 * @return
	 */
	public ThreadPoolExecutor getExecutor();
}