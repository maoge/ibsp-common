package ibsp.common.nio.service.impl;

import ibsp.common.nio.service.Connection;

/**
 * 扫描无效的callBack任务
 */
public class InvalidCallBackScanTask implements ScanTask {
	public void visit(final long now, final Connection conn) {
		if (conn.isConnected()) {
			((DefaultConnection) conn).removeAllInvalidRequestCallBack();
		}
	}
}