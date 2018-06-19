package ibsp.common.nio.service.udp;

import java.net.DatagramPacket;

/**
 * UDP消息处理器
 */
public interface UDPServiceHandler {
	public void onMessageReceived(DatagramPacket datagramPacket);
}