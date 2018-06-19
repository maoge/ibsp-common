package ibsp.common.nio.service.udp.impl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;

import ibsp.common.nio.core.core.Session;
import ibsp.common.nio.core.core.impl.HandlerAdapter;
import ibsp.common.nio.core.core.impl.StandardSocketOption;
import ibsp.common.nio.core.nio.UDPController;
import ibsp.common.nio.core.nio.impl.DatagramChannelController;
import ibsp.common.nio.core.nio.impl.NioController;
import ibsp.common.nio.service.RemotingController;
import ibsp.common.nio.service.exception.NotifyRemotingException;
import ibsp.common.nio.service.impl.BaseRemotingController;
import ibsp.common.nio.service.udp.UDPServer;
import ibsp.common.nio.service.udp.UDPServiceHandler;

/**
 * UDP服务端实现
 */
public class DefaultUDPServer implements UDPServer {
	protected DatagramChannelController udpController;
	protected final UDPServiceHandler handler;

	public DefaultUDPServer(final UDPServiceHandler handler, final int port) throws NotifyRemotingException {
		this(null, handler, port);
	}

	public DefaultUDPServer(final RemotingController remotingController, final UDPServiceHandler handler, final int port)
			throws NotifyRemotingException {
		this.initController();
		this.handler = handler;
		this.udpController.setHandler(new HandlerAdapter() {

			@Override
			public void onMessageReceived(final Session session, final Object message) {
				DefaultUDPServer.this.handler.onMessageReceived((DatagramPacket) message);
			}

		});
		// 复用SelectorManager
		if (remotingController != null && remotingController.isStarted()) {
			final NioController nioController = ((BaseRemotingController) remotingController).getController();
			this.udpController.setSelectorManager(nioController.getSelectorManager());
		}
		try {
			this.udpController.bind(new InetSocketAddress(port));
		} catch (final IOException e) {
			throw new NotifyRemotingException("启动udp服务器失败，端口为" + port, e);
		}

	}

	public boolean isStarted() {
		return this.udpController.isStarted();
	}

	protected void initController() {
		this.udpController = new UDPController();
		this.udpController.setSocketOption(StandardSocketOption.SO_REUSEADDR, true);
	}

	public UDPServiceHandler getUDPServiceHandler() {
		return this.handler;
	}

	public void start() throws NotifyRemotingException {
		try {
			this.udpController.start();
		} catch (final IOException e) {
			throw new NotifyRemotingException(e);
		}

	}

	public void stop() throws NotifyRemotingException {
		try {
			this.udpController.stop();
		} catch (final IOException e) {
			throw new NotifyRemotingException(e);
		}

	}

}