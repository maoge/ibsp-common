package ibsp.common.nio.core.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import ibsp.common.nio.core.nio.impl.TimerRef;

/**
 * SelectionKey处理器
 */
public interface SelectionKeyHandler {
	public void onAccept(SelectionKey sk) throws IOException;

	public void closeSelectionKey(SelectionKey key);

	public void onWrite(SelectionKey key);

	public void onRead(SelectionKey key);

	public void onTimeout(TimerRef timerRef);

	public void onConnect(SelectionKey key) throws IOException;

	public void closeChannel(Selector selector) throws IOException;
}