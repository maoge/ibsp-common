package ibsp.common.nio.core.nio.input;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import ibsp.common.nio.core.util.ByteBufferUtils;

/**
 * 流式API读
 */
public class ChannelInputStream extends InputStream {
	public final ByteBuffer messageBuffer;

	public ChannelInputStream(final ByteBuffer messageBuffer) throws IOException {
		super();
		if (messageBuffer == null) {
			throw new IOException("Null messageBuffer");
		}
		this.messageBuffer = messageBuffer;
	}

	@Override
	public int read() throws IOException {
		if (this.messageBuffer.remaining() == 0) {
			return -1;
		}
		return ByteBufferUtils.uByte(this.messageBuffer.get());
	}

	@Override
	public int available() throws IOException {
		return this.messageBuffer.remaining();
	}

	@Override
	public synchronized void mark(final int readlimit) {
		this.messageBuffer.mark();
	}

	@Override
	public boolean markSupported() {
		return true;
	}

	@Override
	public synchronized void reset() throws IOException {
		this.messageBuffer.reset();
	}

	@Override
	public void close() throws IOException {
		throw new UnsupportedOperationException("Please use Session.close() to close iostream.");
	}

}