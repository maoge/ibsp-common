package ibsp.common.nio.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ibsp.common.nio.service.Connection;

/**
 * 扫描无效的连接任务，仅用于服务器
 */
public class InvalidConnectionScanTask implements ScanTask {

	private static Logger logger = LoggerFactory.getLogger(InvalidConnectionScanTask.class);

	// 对于服务器来说，如果5分钟没有任何操作，那么将断开连接，因为客户端总是会发起心跳检测，因此不会对正常的空闲连接误判。
	public static long TIMEOUT_THRESHOLD = Long.parseLong(System.getProperty("notify.remoting.connection.timeout_threshold", "300000"));

	public void visit(final long now, final Connection conn) {
		final long lastOpTimestamp = ((DefaultConnection) conn).getSession().getLastOperationTimeStamp();
		if (now - lastOpTimestamp > TIMEOUT_THRESHOLD) {
			logger.info("无效的连接{}被关闭，超过{}毫秒没有任何IO操作", conn.getRemoteSocketAddress(), TIMEOUT_THRESHOLD);
			try {
				conn.close(false);
			} catch (final Throwable t) {
				logger.error("关闭连接失败:{}", t.getMessage(), t);
			}
		}
	}
}