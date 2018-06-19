package ibsp.common.nio.core.util;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import ibsp.common.nio.core.buffer.IoBuffer;
import ibsp.common.nio.service.config.WireFormatType;

/**
 * Remoting工具类
 */
public class RemotingUtils {

	public static final String getAddrString(final SocketAddress address) {
		if (address == null) {
			return null;
		}
		if (address instanceof InetSocketAddress) {
			final InetSocketAddress socketAddr = (InetSocketAddress) address;
			final InetAddress inetAddr = socketAddr.getAddress();
			return (inetAddr != null ? inetAddr.getHostAddress() : socketAddr.getHostName()) + ":" + socketAddr.getPort();
		}
		return null;
	}

	// 遍历网卡，查找一个非回路ip地址并返回，如果没有找到，则返回InetAddress.getLocalHost()
	public static InetAddress getLocalHostAddress() throws UnknownHostException, SocketException {
		final Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
		InetAddress ipv6Address = null;
		while (enumeration.hasMoreElements()) {
			final NetworkInterface networkInterface = enumeration.nextElement();
			final Enumeration<InetAddress> en = networkInterface.getInetAddresses();
			while (en.hasMoreElements()) {
				final InetAddress address = en.nextElement();
				if (!address.isLoopbackAddress()) {
					if (address instanceof Inet6Address) {
						ipv6Address = address;
					} else {
						// 优先使用ipv4
						return address;
					}
				}
			}

		}
		// 没有ipv4，则使用ipv6
		if (ipv6Address != null) {
			return ipv6Address;
		}
		return InetAddress.getLocalHost();
	}

	public static InetSocketAddress getInetSocketAddressFromURI(final String uriStr) {
		if (uriStr == null) {
			return null;
		}
		try {
			final URI uri = new URI(uriStr);
			return new InetSocketAddress(uri.getHost(), uri.getPort());
		} catch (final URISyntaxException e) {
			// ignore
			return null;
		}
	}

	public static final String formatServerUrl(final WireFormatType wireFormatType, final InetSocketAddress socketAddress) {
		if (socketAddress == null) {
			throw new IllegalArgumentException("Null socketAddress");
		}
		final InetAddress inetAddress = socketAddress.getAddress();
		return wireFormatType.getScheme() + "://" + (inetAddress == null ? socketAddress.getHostName() : inetAddress.getHostAddress())
				+ ":" + socketAddress.getPort();
	}

	public static final String formatServerUrl(final WireFormatType wireFormatType, final String hostNmae, final int port) {
		if (StringUtils.isEmpty(hostNmae)) {
			throw new IllegalArgumentException("Blank hostName");
		}
		return wireFormatType.getScheme() + "://" + hostNmae + ":" + port;
	}

	public static String dumpBuffer(final IoBuffer buff) {
		final StringBuilder sb = new StringBuilder("[Buffer \n");
		final int pos = buff.position();
		int count = 0;
		for (int i = pos; i < buff.limit(); i++, count++) {
			RemotingUtils.byte2hex(buff.get(i), sb);
			sb.append(" ");
			if (count > 0 && count % 16 == 0) {
				sb.append("\n");
			}
		}
		sb.append("\n]");
		return sb.toString();
	}

	public static void byte2hex(final byte b, final StringBuilder buf) {
		final char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		final int high = (b & 0xf0) >> 4;
		final int low = b & 0x0f;
		buf.append(hexChars[high]);
		buf.append(hexChars[low]);
	}

}