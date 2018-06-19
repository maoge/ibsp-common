package ibsp.common.nio.service.impl;

import ibsp.common.nio.service.Connection;

/**
 * 全局扫描的任务接口
 */
public interface ScanTask {
	/**
	 * 
	 * @param now
	 *            扫描触发的时间点
	 * @param conn
	 *            当前扫描到的连接
	 */
	public void visit(long now, Connection conn);
}