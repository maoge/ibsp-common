package ibsp.common.nio.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import ibsp.common.nio.core.util.MBeanUtils;
import ibsp.common.nio.service.Connection;

/**
 * 分组管理器,管理分组到连接的映射关系
 */

public class GroupManager implements GroupManagerMBean {
	private final ConcurrentHashMap<String/* group */, List<Connection>> group2ConnectionMap = new ConcurrentHashMap<String, List<Connection>>();

	public GroupManager() {
		MBeanUtils.registerMBeanWithIdPrefix(this, null);
	}

	public boolean addConnection(final String group, final Connection connection) {
		List<Connection> connections = this.group2ConnectionMap.get(group);
		if (connections == null) {
			// 采用copyOnWrite主要是考虑遍历connection的操作会多一些，在发送消息的时候
			connections = new CopyOnWriteArrayList<Connection>();
			final List<Connection> oldList = this.group2ConnectionMap.putIfAbsent(group, connections);
			if (oldList != null) {
				connections = oldList;
			}
		}
		synchronized (connections) {
			// 已经包含，即认为添加成功
			if (connections.contains(connection)) {
				return true;
			} else {
				((DefaultConnection) connection).addGroup(group);
				return connections.add(connection);
			}
		}
	}

	public Map<String, Set<String>> getGroupConnectionInfo() {
		final Map<String, Set<String>> result = new HashMap<String, Set<String>>();
		for (final Map.Entry<String, List<Connection>> entry : this.group2ConnectionMap.entrySet()) {
			final Set<String> set = new HashSet<String>();
			if (entry.getValue() != null) {
				for (final Connection conn : entry.getValue()) {
					set.add(conn.toString());
				}
			}
			result.put(entry.getKey(), set);
		}
		return result;
	}

	public void clear() {
		this.group2ConnectionMap.clear();
	}

	public int getGroupConnectionCount(final String group) {
		final List<Connection> connections = this.group2ConnectionMap.get(group);
		if (connections == null) {
			return 0;
		} else {
			synchronized (connections) {
				return connections.size();
			}
		}
	}

	public boolean removeConnection(final String group, final Connection connection) {
		final List<Connection> connections = this.group2ConnectionMap.get(group);
		if (connections == null) {
			return false;
		} else {
			synchronized (connections) {
				final boolean result = connections.remove(connection);
				if (result) {
					((DefaultConnection) connection).removeGroup(group);
				}
				if (connections.size() == 0) {
					this.group2ConnectionMap.remove(group);
				}
				return result;
			}
		}

	}

	public Set<String> getGroupSet() {
		return this.group2ConnectionMap.keySet();
	}

	public List<Connection> getConnectionsByGroup(final String group) {
		return this.group2ConnectionMap.get(group);
	}

}