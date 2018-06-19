package ibsp.common.nio.service.impl;

import ibsp.common.nio.core.command.RequestCommand;
import ibsp.common.nio.core.command.ResponseCommand;
import ibsp.common.nio.service.Connection;

/**
 * 请求回调的公共接口
 */

public interface RequestCallBack {

	/**
	 * 判断回调是否过期
	 * 
	 * @param now
	 *            当前时间
	 * @return
	 */
	public boolean isInvalid(long now);

	/**
	 * 当响应到达的时，触发此方法
	 * 
	 * @param group
	 *            应答的分组名
	 * @param responseCommand
	 *            应答命令
	 * @param connection
	 *            应答的连接
	 */
	public void onResponse(String group, ResponseCommand responseCommand, Connection connection);

	/**
	 * 设置异常
	 * 
	 * @param e
	 * @param conn
	 * @param requestCommand
	 */
	public void setException(Exception e, Connection conn, RequestCommand requestCommand);

	public void dispose();
}