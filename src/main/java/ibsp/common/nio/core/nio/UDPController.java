package ibsp.common.nio.core.nio;

import java.io.IOException;

import ibsp.common.nio.core.config.Configuration;
import ibsp.common.nio.core.core.CodecFactory;
import ibsp.common.nio.core.core.Handler;
import ibsp.common.nio.core.core.ServerController;
import ibsp.common.nio.core.nio.impl.DatagramChannelController;

/**
 * Controller for udp server
 */
public class UDPController extends DatagramChannelController implements ServerController {

	public UDPController(final Configuration configuration) {
		super(configuration, null, null);
		this.setMaxDatagramPacketLength(configuration.getSessionReadBufferSize() > 9216 ? 4096 : configuration.getSessionReadBufferSize());
	}

	public UDPController() {
		super();
	}

	public UDPController(final Configuration configuration, final CodecFactory codecFactory) {
		super(configuration, null, codecFactory);
		this.setMaxDatagramPacketLength(configuration.getSessionReadBufferSize() > 9216 ? 4096 : configuration.getSessionReadBufferSize());
	}

	public void unbind() throws IOException {
		this.stop();
	}

	public UDPController(final Configuration configuration, final Handler handler, final CodecFactory codecFactory) {
		super(configuration, handler, codecFactory);
		this.setMaxDatagramPacketLength(configuration.getSessionReadBufferSize() > 9216 ? 4096 : configuration.getSessionReadBufferSize());
	}
}