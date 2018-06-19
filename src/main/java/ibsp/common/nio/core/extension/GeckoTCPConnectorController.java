package ibsp.common.nio.core.extension;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ibsp.common.nio.core.config.Configuration;
import ibsp.common.nio.core.core.CodecFactory;
import ibsp.common.nio.core.core.EventType;
import ibsp.common.nio.core.core.Handler;
import ibsp.common.nio.core.core.impl.FutureImpl;
import ibsp.common.nio.core.nio.NioSession;
import ibsp.common.nio.core.nio.impl.SocketChannelController;
import ibsp.common.nio.service.RemotingClient;

/**
 * 连接管理器，扩展SocketChannelController，提供单个Controller管理多个客户端连接功能
 */

public class GeckoTCPConnectorController extends SocketChannelController {

	private static Logger logger = LoggerFactory.getLogger(GeckoTCPConnectorController.class);

	/**
	 * 连接失败监听器
	 */
	private ConnectFailListener connectFailListener;

	public ConnectFailListener getConnectFailListener() {
		return this.connectFailListener;
	}

	public void setConnectFailListener(final ConnectFailListener connectFailListener) {
		this.connectFailListener = connectFailListener;
	}

	public GeckoTCPConnectorController(final RemotingClient remotingClient) {
		super();
	}

	public FutureImpl<NioSession> connect(final InetSocketAddress remoteAddress, final Object... args) throws IOException {
		SocketChannel socketChannel = null;
		try {
			socketChannel = SocketChannel.open();
			this.configureSocketChannel(socketChannel);
			final FutureImpl<NioSession> resultFuture = new FutureImpl<NioSession>(args);
			if (!socketChannel.connect(remoteAddress)) {
				this.selectorManager.registerChannel(socketChannel, SelectionKey.OP_CONNECT, resultFuture);
			} else {
				final NioSession session = this.createSession(socketChannel, args);
				resultFuture.setResult(session);
			}
			return resultFuture;
		} catch (final IOException e) {
			if (socketChannel != null) {
				socketChannel.close();
			}
			throw e;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onConnect(final SelectionKey key) throws IOException {
		key.interestOps(key.interestOps() & ~SelectionKey.OP_CONNECT);
		final FutureImpl<NioSession> future = (FutureImpl<NioSession>) key.attachment();
		key.attach(null);
		try {
			if (!((SocketChannel) key.channel()).finishConnect()) {
				throw new IOException("Connect Fail");
			}
			future.setResult(this.createSession((SocketChannel) key.channel(), future.getArgs()));
		} catch (final Exception e) {
			this.cancelKey(key);
			future.failure(e);
			logger.error("Connect Fail, errmsg: {}", e.getMessage());
			// 通知连接失败
			if (this.connectFailListener != null) {
				this.connectFailListener.onConnectFail(future.getArgs());
			}
		}
	}

	private void cancelKey(final SelectionKey key) throws IOException {
		try {
			if (key.channel() != null) {
				key.channel().close();
			}
		} finally {
			key.cancel();
		}
	}

	protected NioSession createSession(final SocketChannel socketChannel, final Object... args) {
		final NioSession session = this.buildSession(socketChannel);
		this.selectorManager.registerSession(session, EventType.ENABLE_READ);
		this.setLocalSocketAddress((InetSocketAddress) socketChannel.socket().getLocalSocketAddress());
		session.start();
		this.handler.onSessionConnected(session, args);
		return session;
	}

	public GeckoTCPConnectorController(final Configuration configuration, final CodecFactory codecFactory) {
		super(configuration, codecFactory);
	}

	public GeckoTCPConnectorController(final Configuration configuration, final Handler handler, final CodecFactory codecFactory) {
		super(configuration, handler, codecFactory);
	}

	public GeckoTCPConnectorController(final Configuration configuration) {
		super(configuration);
	}

	@Override
	protected void doStart() throws IOException {
		// do nothing
	}

	public void closeChannel(final Selector selector) throws IOException {

	}

}