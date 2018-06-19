package ibsp.common.nio.core.core.impl;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;

import ibsp.common.nio.core.buffer.IoBuffer;
import ibsp.common.nio.core.core.WriteMessage;

/**
 * 写消息毒丸，当写到此消息的时候，连接将关闭
 */
public class PoisonWriteMessage implements WriteMessage {
	static IoBuffer EMPTY = IoBuffer.allocate(0);

	public Object getMessage() {

		return null;
	}

	public long remaining() {
		return 0;
	}

	public FutureImpl<Boolean> getWriteFuture() {

		return null;
	}

	public boolean hasRemaining() {
		return false;
	}

	public long write(final WritableByteChannel channel) throws IOException {
		return 0;
	}

	public boolean isWriting() {

		return false;
	}

	public void writing() {

	}

}