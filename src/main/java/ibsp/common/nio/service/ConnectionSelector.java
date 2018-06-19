package ibsp.common.nio.service;

import java.util.List;

import ibsp.common.nio.core.command.RequestCommand;
import ibsp.common.nio.service.exception.NotifyRemotingException;

/**
 * 选择连接策略
 */

public interface ConnectionSelector {
	/**
	 * 从分组的连接列表中选择想要的连接
	 * 
	 * @param targetGroup
	 *            分组名称
	 * @param request
	 *            请求命令
	 * @param connectionList
	 *            分组的连接列表
	 * @return
	 */
	public Connection select(String targetGroup, RequestCommand request, List<Connection> connectionList) throws NotifyRemotingException;
}