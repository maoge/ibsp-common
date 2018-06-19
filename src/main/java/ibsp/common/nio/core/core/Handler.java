package ibsp.common.nio.core.core;

/**
 * 业务处理器
 */
public interface Handler {

	void onSessionCreated(Session session);

	void onSessionStarted(Session session);

	void onSessionClosed(Session session);

	void onMessageReceived(Session session, Object msg);

	void onMessageSent(Session session, Object msg);

	void onExceptionCaught(Session session, Throwable throwable);

	void onSessionExpired(Session session);

	void onSessionIdle(Session session);

	void onSessionConnected(Session session, Object[] args);

}