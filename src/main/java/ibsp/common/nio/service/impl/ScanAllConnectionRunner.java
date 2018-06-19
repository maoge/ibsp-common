package ibsp.common.nio.service.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ibsp.common.nio.core.command.Constants;
import ibsp.common.nio.service.Connection;

/**
 * 扫描所有连接的任务线程
 */

public class ScanAllConnectionRunner implements Runnable {
	private final BaseRemotingController controller;

	private final CopyOnWriteArrayList<ScanTask> taskList = new CopyOnWriteArrayList<ScanTask>();

	public void addScanTask(final ScanTask task) {
		this.taskList.add(task);
	}

	public void removeScanTask(final ScanTask task) {
		this.taskList.remove(task);
	}

	public ScanAllConnectionRunner(final BaseRemotingController controller, final ScanTask... tasks) {
		super();
		this.controller = controller;
		if (tasks != null) {
			for (final ScanTask task : tasks) {
				this.taskList.add(task);
			}
		}

	}

	public void run() {
		// 获取所有连接并遍历
		final long now = System.currentTimeMillis();
		final List<Connection> connections = this.controller.remotingContext.getConnectionsByGroup(Constants.DEFAULT_GROUP);
		if (connections != null) {
			for (final Connection conn : connections) {
				for (final ScanTask task : this.taskList) {
					task.visit(now, conn);
				}
			}
		}

	}

}