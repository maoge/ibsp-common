package ibsp.common.nio.service;

import java.util.List;
import java.util.Set;

import ibsp.common.nio.core.command.CommandFactory;
import ibsp.common.nio.service.config.BaseConfig;

/**
 * 
 * Remoting的全局上下文
 * 
 * @author boyan
 * 
 * @since 1.0, 2009-12-15 下午03:54:01
 */

public interface RemotingContext {

	/**
	 * 添加连接到指定分组
	 * 
	 * @param group
	 * @param connection
	 * @return
	 */
	public abstract boolean addConnectionToGroup(String group, Connection connection);

	/**
	 * 获取当前的网络层配置对象
	 * 
	 * @return
	 */
	public abstract BaseConfig getConfig();

	/**
	 * 添加到默认分组
	 * 
	 * @param connection
	 */
	public abstract void addConnection(Connection connection);

	/**
	 * 从默认分组移除
	 * 
	 * @param connection
	 */
	public abstract void removeConnection(Connection connection);

	/**
	 * 根据Group得到connection集合
	 * 
	 * @param group
	 * @return
	 */
	public abstract List<Connection> getConnectionsByGroup(String group);

	/**
	 * 移除连接
	 * 
	 * @param group
	 * @param connection
	 * @return
	 */
	public abstract boolean removeConnectionFromGroup(String group, Connection connection);

	public abstract Object getAttribute(Object key, Object defaultValue);

	public abstract Object getAttribute(Object key);

	public abstract Set<Object> getAttributeKeys();

	public abstract Object setAttribute(Object key, Object value);

	public abstract Object setAttributeIfAbsent(Object key, Object value);

	public abstract Object setAttributeIfAbsent(Object key);

	/**
	 * 获取当前客户端或者服务器的所有分组名称
	 * 
	 * @return
	 */
	public Set<String> getGroupSet();

	/**
	 * 获取当前使用的协议工厂
	 * 
	 * @return
	 */
	public CommandFactory getCommandFactory();

}