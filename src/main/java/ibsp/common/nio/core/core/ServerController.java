package ibsp.common.nio.core.core;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * 针对服务器的控制接口
 */
public interface ServerController extends Controller {

	public void bind(InetSocketAddress localAddress) throws IOException;

	public void bind(int port) throws IOException;

	public void unbind() throws IOException;

}