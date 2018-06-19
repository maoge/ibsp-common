package ibsp.common.nio.service;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import ibsp.common.nio.core.command.ResponseCommand;

/**
 * 单分组所有连接的回调监听器
 */

public interface GroupAllConnectionCallBackListener {

	/**
	 * 处理应答
	 * 
	 * @param resultMap
	 */
	public void onResponse(Map<Connection, ResponseCommand> resultMap);

	public ThreadPoolExecutor getExecutor();
}