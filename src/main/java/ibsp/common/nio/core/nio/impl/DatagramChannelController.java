package ibsp.common.nio.core.nio.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ibsp.common.nio.core.config.Configuration;
import ibsp.common.nio.core.core.CodecFactory;
import ibsp.common.nio.core.core.EventType;
import ibsp.common.nio.core.core.Handler;
import ibsp.common.nio.core.core.WriteMessage;
import ibsp.common.nio.core.core.impl.StandardSocketOption;
import ibsp.common.nio.core.nio.NioSession;
import ibsp.common.nio.core.util.SystemUtils;

/**
 * Nio的UDP实现
 */
public abstract class DatagramChannelController extends NioController {

	private static Logger logger = LoggerFactory.getLogger(DatagramChannelController.class);

	protected DatagramChannel channel;
	protected NioSession udpSession;
	protected int maxDatagramPacketLength;

	public DatagramChannelController() {
		super();
		this.maxDatagramPacketLength = 4096;
	}

	@Override
	protected void doStart() throws IOException {
		this.buildDatagramChannel();
		this.initialSelectorManager();
		this.buildUDPSession();
	}

	public DatagramChannelController(final Configuration configuration) {
		super(configuration, null, null);
		this.setMaxDatagramPacketLength(configuration.getSessionReadBufferSize() > 9216 ? 4096 : configuration.getSessionReadBufferSize());
	}

	public DatagramChannelController(final Configuration configuration, final CodecFactory codecFactory) {
		super(configuration, null, codecFactory);
		this.setMaxDatagramPacketLength(configuration.getSessionReadBufferSize() > 9216 ? 4096 : configuration.getSessionReadBufferSize());
	}

	public DatagramChannelController(final Configuration configuration, final Handler handler, final CodecFactory codecFactory) {
		super(configuration, handler, codecFactory);
		this.setMaxDatagramPacketLength(configuration.getSessionReadBufferSize() > 9216 ? 4096 : configuration.getSessionReadBufferSize());
	}

	public int getMaxDatagramPacketLength() {
		return this.maxDatagramPacketLength;
	}

	@Override
	public void setReadThreadCount(final int readThreadCount) {
		if (readThreadCount > 1) {
			throw new IllegalArgumentException("UDP controller could not have more than 1 read thread");
		}
		super.setReadThreadCount(readThreadCount);
	}

	public void setMaxDatagramPacketLength(final int maxDatagramPacketLength) {
		if (this.isStarted()) {
			throw new IllegalStateException();
		}
		if (SystemUtils.isLinuxPlatform() && maxDatagramPacketLength > 9216) {
			throw new IllegalArgumentException("The maxDatagramPacketLength could not be larger than 9216 bytes on linux");
		} else if (maxDatagramPacketLength > 65507) {
			throw new IllegalArgumentException("The maxDatagramPacketLength could not be larger than 65507 bytes");
		}
		this.maxDatagramPacketLength = maxDatagramPacketLength;
	}

	public void closeChannel(final Selector selector) throws IOException {
		this.closeChannel0();
		selector.selectNow();
	}

	private void closeChannel0() throws IOException {
		if (this.udpSession != null && !this.udpSession.isClosed()) {
			this.udpSession.close();
			this.udpSession = null;
		}
		if (this.channel != null && this.channel.isOpen()) {
			this.channel.close();
			this.channel = null;
		}
	}

	@Override
	protected void stop0() throws IOException {
		this.closeChannel0();
		super.stop0();
	}

	protected void buildUDPSession() {
		final Queue<WriteMessage> queue = this.buildQueue();
		this.udpSession = new NioUDPSession(this.buildSessionConfig(this.channel, queue), this.maxDatagramPacketLength);
		this.selectorManager.registerSession(this.udpSession, EventType.ENABLE_READ);
		this.udpSession.start();
	}

	protected void buildDatagramChannel() throws IOException, SocketException, ClosedChannelException {
		this.channel = DatagramChannel.open();
		this.channel.socket().setSoTimeout(this.soTimeout);

		if (this.socketOptions.get(StandardSocketOption.SO_REUSEADDR) != null) {
			this.channel.socket().setReuseAddress(
					StandardSocketOption.SO_REUSEADDR.type().cast(this.socketOptions.get(StandardSocketOption.SO_REUSEADDR)));
		}
		if (this.socketOptions.get(StandardSocketOption.SO_RCVBUF) != null) {
			this.channel.socket().setReceiveBufferSize(
					StandardSocketOption.SO_RCVBUF.type().cast(this.socketOptions.get(StandardSocketOption.SO_RCVBUF)));

		}
		if (this.socketOptions.get(StandardSocketOption.SO_SNDBUF) != null) {
			this.channel.socket().setSendBufferSize(
					StandardSocketOption.SO_SNDBUF.type().cast(this.socketOptions.get(StandardSocketOption.SO_SNDBUF)));
		}
		this.channel.configureBlocking(false);
		if (this.localSocketAddress != null) {
			this.channel.socket().bind(this.localSocketAddress);
		} else {
			this.channel.socket().bind(new InetSocketAddress("localhost", 0));
		}
		this.setLocalSocketAddress((InetSocketAddress) this.channel.socket().getLocalSocketAddress());

	}

	@Override
	protected void dispatchReadEvent(final SelectionKey key) {
		if (this.udpSession != null) {
			this.udpSession.onEvent(EventType.READABLE, key.selector());
		} else {
			logger.error("NO session to dispatch read event");
		}

	}

	@Override
	protected void dispatchWriteEvent(final SelectionKey key) {
		if (this.udpSession != null) {
			this.udpSession.onEvent(EventType.WRITEABLE, key.selector());
		} else {
			logger.error("NO session to dispatch write event");
		}

	}

}