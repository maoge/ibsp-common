package ibsp.common.nio.core.nio.output;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.Future;

import ibsp.common.nio.core.buffer.IoBuffer;
import ibsp.common.nio.core.nio.impl.NioTCPSession;

/**
 * 流式API写
 */
public class ChannelOutputStream extends OutputStream {
	private final ByteBuffer writeBuffer;
	private final NioTCPSession session;

	public ChannelOutputStream(final NioTCPSession session, final int capacity, final boolean direct) {
		if (direct) {
			this.writeBuffer = ByteBuffer.allocateDirect(capacity <= 0 ? 1024 : capacity);
		} else {
			this.writeBuffer = ByteBuffer.allocate(capacity <= 0 ? 1024 : capacity);
		}
		this.session = session;
	}

	@Override
	public void write(final int b) throws IOException {
		this.writeBuffer.put((byte) b);

	}

	@Override
	public void flush() throws IOException {
		this.writeBuffer.flip();
		this.session.write(IoBuffer.wrap(this.writeBuffer));
	}

	public Future<Boolean> asyncFlush() {
		this.writeBuffer.flip();
		return this.session.asyncWrite(IoBuffer.wrap(this.writeBuffer));
	}

	@Override
	public void close() throws IOException {
		throw new UnsupportedOperationException("Please use Session.close() to close iostream.");
	}
}