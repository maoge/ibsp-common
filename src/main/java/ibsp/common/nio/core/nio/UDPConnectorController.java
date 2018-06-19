package ibsp.common.nio.core.nio;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.util.concurrent.Future;

import ibsp.common.nio.core.config.Configuration;
import ibsp.common.nio.core.core.CodecFactory;
import ibsp.common.nio.core.core.Handler;
import ibsp.common.nio.core.core.impl.FutureImpl;
import ibsp.common.nio.core.nio.impl.DatagramChannelController;
import ibsp.common.nio.core.nio.impl.NioUDPSession;

/**
 * Controller for upd client
 */
public class UDPConnectorController extends DatagramChannelController implements SingleConnector {

	protected SocketAddress remoteAddress;

	public synchronized Future<Boolean> connect(final SocketAddress remoteAddress, final Object... args) throws IOException {
		if (remoteAddress == null) {
			throw new NullPointerException("Null remoteAddress");
		}
		this.remoteAddress = remoteAddress;
		if (!this.isStarted()) {
			this.start();
		}
		this.channel.connect(remoteAddress);
		final FutureImpl<Boolean> result = new FutureImpl<Boolean>();
		result.setResult(true);
		return result;

	}

	public boolean isConnected() {
		return this.udpSession != null && !this.udpSession.isClosed();
	}

	public SocketAddress getRemoteAddress() {
		return this.remoteAddress;
	}

	public void disconnect() throws IOException {
		if (this.channel != null) {
			this.channel.disconnect();
		}
		this.remoteAddress = null;
		this.stop();
	}

	public void awaitConnectUnInterrupt() throws IOException {
		// do nothing
	}

	public Future<Boolean> send(final Object msg) {
		throw new UnsupportedOperationException("Please use send(DatagramPacket) insead");
	}

	public Future<Boolean> send(final DatagramPacket packet) {
		if (!this.started) {
			throw new IllegalStateException("Controller has been stopped");
		}
		if (packet == null) {
			throw new NullPointerException("Null package");
		}
		if (this.remoteAddress != null && packet.getAddress() == null) {
			packet.setSocketAddress(this.remoteAddress);
		}
		if (this.remoteAddress == null && packet.getAddress() == null) {
			throw new IllegalArgumentException("Null targetAddress");
		}

		return ((NioUDPSession) this.udpSession).asyncWrite(packet);
	}

	public Future<Boolean> send(final SocketAddress targetAddr, final Object msg) {
		return ((NioUDPSession) this.udpSession).asyncWrite(targetAddr, msg);
	}

	public UDPConnectorController(final Configuration configuration) {
		super(configuration, null, null);
		this.setMaxDatagramPacketLength(configuration.getSessionReadBufferSize());
	}

	public UDPConnectorController() {
		super();
	}

	public UDPConnectorController(final Configuration configuration, final CodecFactory codecFactory) {
		super(configuration, null, codecFactory);
		this.setMaxDatagramPacketLength(configuration.getSessionReadBufferSize());
	}

	public UDPConnectorController(final Configuration configuration, final Handler handler, final CodecFactory codecFactory) {
		super(configuration, handler, codecFactory);
		this.setMaxDatagramPacketLength(configuration.getSessionReadBufferSize());
	}
}