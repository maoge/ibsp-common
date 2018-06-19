package ibsp.common.nio.core.nio;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.Future;

/**
 * 客户端单连接管理器，仅连接单个服务器
 */

public interface SingleConnector {

	public Future<Boolean> connect(SocketAddress socketAddress, Object... args) throws IOException;

	public Future<Boolean> send(Object msg);

	public boolean isConnected();

	public void awaitConnectUnInterrupt() throws IOException;

	public void disconnect() throws IOException;
}