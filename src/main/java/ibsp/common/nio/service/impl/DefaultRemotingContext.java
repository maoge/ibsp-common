package ibsp.common.nio.service.impl;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ibsp.common.nio.core.command.CommandFactory;
import ibsp.common.nio.core.command.Constants;
import ibsp.common.nio.core.command.RequestCommand;
import ibsp.common.nio.core.nio.NioSession;
import ibsp.common.nio.core.util.MBeanUtils;
import ibsp.common.nio.service.Connection;
import ibsp.common.nio.service.ConnectionLifeCycleListener;
import ibsp.common.nio.service.RemotingContext;
import ibsp.common.nio.service.RequestProcessor;
import ibsp.common.nio.service.config.BaseConfig;

/**
 * 通讯层的全局上下文
 */

public class DefaultRemotingContext implements RemotingContext, DefaultRemotingContextMBean {

	private static Logger logger = LoggerFactory.getLogger(DefaultRemotingContext.class);

	private final GroupManager groupManager;
	private final ConcurrentHashMap<Object, Object> attributes = new ConcurrentHashMap<Object, Object>();
	private final BaseConfig config;
	private final CommandFactory commandFactory;
	private final Semaphore callBackSemaphore;
	/**
	 * Session到connection的映射关系
	 */
	protected final ConcurrentHashMap<NioSession, DefaultConnection> session2ConnectionMap = new ConcurrentHashMap<NioSession, DefaultConnection>();
	protected ConcurrentHashMap<Class<? extends RequestCommand>, RequestProcessor<? extends RequestCommand>> processorMap = new ConcurrentHashMap<Class<? extends RequestCommand>, RequestProcessor<? extends RequestCommand>>();

	protected final CopyOnWriteArrayList<ConnectionLifeCycleListener> connectionLifeCycleListenerList = new CopyOnWriteArrayList<ConnectionLifeCycleListener>();

	public DefaultRemotingContext(final BaseConfig config, final CommandFactory commandFactory) {
		this.groupManager = new GroupManager();
		this.config = config;
		if (commandFactory == null) {
			throw new IllegalArgumentException("CommandFactory不能为空");
		}
		this.commandFactory = commandFactory;
		this.callBackSemaphore = new Semaphore(this.config.getMaxCallBackCount());
		MBeanUtils.registerMBeanWithIdPrefix(this, null);
	}

	public int getCallBackCountAvailablePermits() {
		return this.callBackSemaphore.availablePermits();
	}

	public CommandFactory getCommandFactory() {
		return this.commandFactory;
	}

	public BaseConfig getConfig() {
		return this.config;
	}

	/**
	 * 请求允许加入callBack，做callBack总数限制
	 * 
	 * @return
	 */
	boolean aquire() {
		return this.callBackSemaphore.tryAcquire();
	}

	/**
	 * 在应答到达时释放许可
	 */
	void release() {
		this.callBackSemaphore.release();
	}

	void release(final int n) {
		this.callBackSemaphore.release(n);
	}

	void notifyConnectionCreated(final Connection conn) {
		for (final ConnectionLifeCycleListener listener : this.connectionLifeCycleListenerList) {
			try {
				listener.onConnectionCreated(conn);
			} catch (final Throwable t) {
				logger.error("NotifyRemoting-调用ConnectionLifeCycleListener.onConnectionCreated出错, errmsg:{}", t.getMessage());
			}
		}
	}

	void notifyConnectionClosed(final Connection conn) {
		for (final ConnectionLifeCycleListener listener : this.connectionLifeCycleListenerList) {
			try {
				listener.onConnectionClosed(conn);
			} catch (final Throwable t) {
				logger.error("NotifyRemoting-调用ConnectionLifeCycleListener.onConnectionClosed出错, errmsg:{}", t.getMessage());
			}
		}
	}

	void addSession2ConnectionMapping(final NioSession session, final DefaultConnection conn) {
		this.session2ConnectionMap.put(session, conn);
	}

	DefaultConnection getConnectionBySession(final NioSession session) {
		return this.session2ConnectionMap.get(session);
	}

	DefaultConnection removeSession2ConnectionMapping(final NioSession session) {
		return this.session2ConnectionMap.remove(session);
	}

	public Set<String> getGroupSet() {
		return this.groupManager.getGroupSet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taobao.gecko.service.impl.RemotingContext#addConnectionToGroup
	 * (java.lang.String, com.taobao.gecko.service.Connection)
	 */
	public boolean addConnectionToGroup(final String group, final Connection connection) {
		return this.groupManager.addConnection(group, connection);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taobao.gecko.service.impl.RemotingContext#addConnection
	 * (com.taobao.gecko.service.Connection)
	 */
	public void addConnection(final Connection connection) {
		this.groupManager.addConnection(Constants.DEFAULT_GROUP, connection);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taobao.gecko.service.impl.RemotingContext#removeConnection
	 * (com.taobao.gecko.service.Connection)
	 */
	public void removeConnection(final Connection connection) {
		this.groupManager.removeConnection(Constants.DEFAULT_GROUP, connection);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.taobao.notify.remoting.service.impl.RemotingContext#
	 * getConnectionSetByGroup(java.lang.String)
	 */
	public List<Connection> getConnectionsByGroup(final String group) {
		return this.groupManager.getConnectionsByGroup(group);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.taobao.notify.remoting.service.impl.RemotingContext#
	 * removeConnectionFromGroup(java.lang.String,
	 * com.taobao.gecko.service.Connection)
	 */
	public boolean removeConnectionFromGroup(final String group, final Connection connection) {
		return this.groupManager.removeConnection(group, connection);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taobao.gecko.service.impl.RemotingContext#getAttribute(
	 * java.lang.Object, java.lang.Object)
	 */
	public Object getAttribute(final Object key, final Object defaultValue) {
		final Object value = this.attributes.get(key);
		return value == null ? defaultValue : value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taobao.gecko.service.impl.RemotingContext#getAttribute(
	 * java.lang.Object)
	 */
	public Object getAttribute(final Object key) {
		return this.attributes.get(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taobao.gecko.service.impl.RemotingContext#getAttributeKeys ()
	 */
	public Set<Object> getAttributeKeys() {
		return this.attributes.keySet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taobao.gecko.service.impl.RemotingContext#setAttribute(
	 * java.lang.Object, java.lang.Object)
	 */
	public Object setAttribute(final Object key, final Object value) {
		return this.attributes.put(key, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taobao.gecko.service.impl.RemotingContext#setAttribute(
	 * java.lang.Object)
	 */
	public Object setAttribute(final Object key) {
		return this.attributes.put(key, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taobao.gecko.service.impl.RemotingContext#dispose()
	 */
	public void dispose() {
		this.groupManager.clear();
		this.attributes.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taobao.gecko.service.impl.RemotingContext#setAttributeIfAbsent
	 * (java.lang.Object, java.lang.Object)
	 */
	public Object setAttributeIfAbsent(final Object key, final Object value) {
		return this.attributes.putIfAbsent(key, value);
	}

	public Object removeAttribute(final Object key) {
		return this.attributes.remove(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.taobao.gecko.service.impl.RemotingContext#setAttributeIfAbsent
	 * (java.lang.Object)
	 */
	public Object setAttributeIfAbsent(final Object key) {
		return this.attributes.putIfAbsent(key, null);
	}

}