package ibsp.common.nio.service;

import java.util.concurrent.ThreadPoolExecutor;

import ibsp.common.nio.core.command.RequestCommand;

/**
 * 请求处理器
 */

public interface RequestProcessor<T extends RequestCommand> {
	/**
	 * 处理请求
	 * 
	 * @param request
	 *            请求命令
	 * @param conn
	 *            请求来源的连接
	 */
	public void handleRequest(T request, Connection conn);

	/**
	 * 用户自定义的线程池，如果提供，那么请求的处理都将在该线程池内执行
	 * 
	 * @return
	 */
	public ThreadPoolExecutor getExecutor();
}