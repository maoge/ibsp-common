package ibsp.common.nio.core.core;

import java.net.SocketAddress;
import java.util.concurrent.Future;

/**
 * UDP连接抽象
 */
public interface UDPSession extends Session {
	/**
	 * Async write message to another end
	 * 
	 * @param targetAddr
	 * @param packet
	 * @return future
	 */
	public Future<Boolean> asyncWrite(SocketAddress targetAddr, Object packet);

	/**
	 * Write message to another end,do not care when the message is written.
	 * 
	 * @param targetAddr
	 * @param packet
	 */
	public void write(SocketAddress targetAddr, Object packet);
}