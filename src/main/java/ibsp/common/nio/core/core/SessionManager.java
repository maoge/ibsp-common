package ibsp.common.nio.core.core;

/**
 * 连接管理器
 */
public interface SessionManager {
	public void registerSession(Session session);

	public void unregisterSession(Session session);
}