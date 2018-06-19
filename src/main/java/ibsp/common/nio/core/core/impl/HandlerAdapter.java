package ibsp.common.nio.core.core.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ibsp.common.nio.core.core.Handler;
import ibsp.common.nio.core.core.Session;

/**
 * Handler适配器
 */
public class HandlerAdapter implements Handler {

	private static Logger logger = LoggerFactory.getLogger(HandlerAdapter.class);

	public void onExceptionCaught(final Session session, final Throwable throwable) {

	}

	public void onMessageSent(final Session session, final Object message) {

	}

	public void onSessionConnected(final Session session, final Object[] args) {

	}

	public void onSessionStarted(final Session session) {

	}

	public void onSessionCreated(final Session session) {

	}

	public void onSessionClosed(final Session session) {

	}

	public void onMessageReceived(final Session session, final Object message) {

	}

	public void onSessionIdle(final Session session) {

	}

	public void onSessionExpired(final Session session) {
		logger.error("Session ({}) is expired.", session.getRemoteSocketAddress());
	}

}