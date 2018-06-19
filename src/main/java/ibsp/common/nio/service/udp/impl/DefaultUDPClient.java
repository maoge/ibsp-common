package ibsp.common.nio.service.udp.impl;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import ibsp.common.nio.core.buffer.IoBuffer;
import ibsp.common.nio.core.nio.UDPConnectorController;
import ibsp.common.nio.service.RemotingController;
import ibsp.common.nio.service.exception.NotifyRemotingException;
import ibsp.common.nio.service.udp.UDPClient;
import ibsp.common.nio.service.udp.UDPServiceHandler;

/**
 * UDP客户端实现
 */
public class DefaultUDPClient extends DefaultUDPServer implements UDPClient {

	public DefaultUDPClient(final UDPServiceHandler handler) throws NotifyRemotingException {
		super(handler, 0);

	}

	public DefaultUDPClient(final RemotingController remotingController, final UDPServiceHandler handler) throws NotifyRemotingException {
		super(remotingController, handler, 0);
	}

	public void send(final InetSocketAddress inetSocketAddress, final ByteBuffer buff) throws NotifyRemotingException {
		try {
			((UDPConnectorController) this.udpController).send(inetSocketAddress, IoBuffer.wrap(buff));
		} catch (final Throwable t) {
			throw new NotifyRemotingException(t);
		}
	}

	@Override
	protected void initController() {
		this.udpController = new UDPConnectorController();
	}

}