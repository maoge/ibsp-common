package ibsp.common.nio.core.core.impl;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;

import ibsp.common.nio.core.buffer.IoBuffer;
import ibsp.common.nio.core.core.WriteMessage;

/**
 * 发送消息包装实现
 */
public class ByteBufferWriteMessage implements WriteMessage {

	protected Object message;

	protected IoBuffer buffer;

	protected FutureImpl<Boolean> writeFuture;

	protected volatile boolean writing;

	public final void writing() {
		this.writing = true;
	}

	public final boolean isWriting() {
		return this.writing;
	}

	public ByteBufferWriteMessage(final Object message, final FutureImpl<Boolean> writeFuture) {
		this.message = message;
		this.writeFuture = writeFuture;
	}

	public long remaining() {
		return this.buffer == null ? 0 : this.buffer.remaining();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.code.yanf4j.nio.IWriteMessage#getBuffers()
	 */
	public synchronized final IoBuffer getWriteBuffer() {
		return this.buffer;
	}

	public boolean hasRemaining() {
		return this.buffer != null && this.buffer.hasRemaining();
	}

	public long write(final WritableByteChannel channel) throws IOException {
		return channel.write(this.buffer.buf());
	}

	public synchronized final void setWriteBuffer(final IoBuffer buffers) {
		this.buffer = buffers;

	}

	public final FutureImpl<Boolean> getWriteFuture() {
		return this.writeFuture;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.code.yanf4j.nio.IWriteMessage#getMessage()
	 */
	public final Object getMessage() {
		return this.message;
	}
}