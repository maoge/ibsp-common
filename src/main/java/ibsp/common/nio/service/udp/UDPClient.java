package ibsp.common.nio.service.udp;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import ibsp.common.nio.service.exception.NotifyRemotingException;

/**
 * UDP客户端
 */
public interface UDPClient extends UDPController {

	/**
	 * 发送UDP消息到指定IP
	 * 
	 * @param buffer
	 */
	public void send(InetSocketAddress inetSocketAddress, ByteBuffer buffer) throws NotifyRemotingException;

}