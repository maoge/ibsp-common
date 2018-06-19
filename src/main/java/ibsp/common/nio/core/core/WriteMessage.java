package ibsp.common.nio.core.core;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;

import ibsp.common.nio.core.core.impl.FutureImpl;

/**
 * 发送消息包装类
 */
public interface WriteMessage {

	void writing();

	boolean isWriting();

	Object getMessage();

	public boolean hasRemaining();

	public long remaining();

	public long write(WritableByteChannel channel) throws IOException;

	FutureImpl<Boolean> getWriteFuture();

}