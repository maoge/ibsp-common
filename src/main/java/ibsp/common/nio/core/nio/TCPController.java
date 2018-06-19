package ibsp.common.nio.core.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ibsp.common.nio.core.config.Configuration;
import ibsp.common.nio.core.core.CodecFactory;
import ibsp.common.nio.core.core.EventType;
import ibsp.common.nio.core.core.Handler;
import ibsp.common.nio.core.core.ServerController;
import ibsp.common.nio.core.core.Session;
import ibsp.common.nio.core.core.impl.StandardSocketOption;
import ibsp.common.nio.core.nio.impl.SocketChannelController;

/**
 * Controller for tcp server
 */
public class TCPController extends SocketChannelController implements ServerController {

	private static Logger logger = LoggerFactory.getLogger(TCPController.class);

	private ServerSocketChannel serverSocketChannel;

	/**
	 * Accept backlog queue size
	 */
	private int backlog = 500; // default 500

	public int getBacklog() {
		return this.backlog;
	}

	public void setBacklog(final int backlog) {
		if (this.isStarted()) {
			throw new IllegalStateException();
		}
		if (backlog < 0) {
			throw new IllegalArgumentException("backlog<0");
		}
		this.backlog = backlog;
	}

	public TCPController() {
		super();
	}

	public TCPController(final Configuration configuration) {
		super(configuration, null, null);

	}

	public TCPController(final Configuration configuration, final CodecFactory codecFactory) {
		super(configuration, null, codecFactory);
	}

	public TCPController(final Configuration configuration, final Handler handler, final CodecFactory codecFactory) {
		super(configuration, handler, codecFactory);
	}

	private int connectionTime, latency, bandwidth;

	public void setPerformancePreferences(final int connectionTime, final int latency, final int bandwidth) {
		this.connectionTime = connectionTime;
		this.latency = latency;
		this.bandwidth = bandwidth;
	}

	@Override
	protected void doStart() throws IOException {
		this.serverSocketChannel = ServerSocketChannel.open();
		this.serverSocketChannel.socket().setSoTimeout(this.soTimeout);
		if (this.connectionTime != 0 || this.latency != 0 || this.bandwidth != 0) {
			this.serverSocketChannel.socket().setPerformancePreferences(this.connectionTime, this.latency, this.bandwidth);
		}
		this.serverSocketChannel.configureBlocking(false);

		if (this.socketOptions.get(StandardSocketOption.SO_REUSEADDR) != null) {
			this.serverSocketChannel.socket().setReuseAddress(
					StandardSocketOption.SO_REUSEADDR.type().cast(this.socketOptions.get(StandardSocketOption.SO_REUSEADDR)));
		}
		if (this.socketOptions.get(StandardSocketOption.SO_RCVBUF) != null) {
			this.serverSocketChannel.socket().setReceiveBufferSize(
					StandardSocketOption.SO_RCVBUF.type().cast(this.socketOptions.get(StandardSocketOption.SO_RCVBUF)));

		}
		if (this.localSocketAddress != null) {
			this.serverSocketChannel.socket().bind(this.localSocketAddress, this.backlog);
		} else {
			this.serverSocketChannel.socket().bind(new InetSocketAddress("localhost", 0), this.backlog);
		}
		this.setLocalSocketAddress((InetSocketAddress) this.serverSocketChannel.socket().getLocalSocketAddress());
		this.selectorManager.registerChannel(this.serverSocketChannel, SelectionKey.OP_ACCEPT, null);
	}

	@Override
	public void onAccept(final SelectionKey selectionKey) throws IOException {
		// Server已经关闭，直接返回
		if (!this.serverSocketChannel.isOpen()) {
			selectionKey.cancel();
			return;
		}
		SocketChannel sc = null;
		try {
			sc = this.serverSocketChannel.accept();
			if (sc != null) {
				this.configureSocketChannel(sc);
				final Session session = this.buildSession(sc);
				// enable read
				this.selectorManager.registerSession(session, EventType.ENABLE_READ);
				session.start();
				super.onAccept(selectionKey); // for statistics
			} else {
				logger.error("Accept fail");
			}
		} catch (final IOException e) {
			this.closeAcceptChannel(selectionKey, sc);
			this.notifyException(e);
		}
	}

	/**
	 * 
	 * @param sk
	 * @param sc
	 * @throws IOException
	 * @throws SocketException
	 */
	private void closeAcceptChannel(final SelectionKey sk, final SocketChannel sc) throws IOException, SocketException {
		if (sk != null) {
			sk.cancel();
		}
		if (sc != null) {
			sc.socket().setSoLinger(true, 0); // await TIME_WAIT status
			sc.socket().shutdownOutput();
			sc.close();
		}
	}

	@Override
	protected void stop0() throws IOException {
		this.closeServerChannel();
		super.stop0();
	}

	public void closeChannel(final Selector selector) throws IOException {
		this.closeServerChannel();
	}

	private void closeServerChannel() throws IOException {
		if (this.serverSocketChannel != null && this.serverSocketChannel.isOpen()) {
			this.serverSocketChannel.close();
		}
	}

	public void unbind() throws IOException {
		this.stop();
	}

}