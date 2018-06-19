package ibsp.common.nio.service.callback;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import ibsp.common.nio.core.command.RequestCommand;
import ibsp.common.nio.core.command.ResponseCommand;
import ibsp.common.nio.service.Connection;
import ibsp.common.nio.service.GroupAllConnectionCallBackListener;

/**
 * 单个分组所有连接的请求回调
 */

public class GroupAllConnectionRequestCallBack extends AbstractRequestCallBack {
	private final ConcurrentHashMap<Connection, ResponseCommand> resultMap; // 应答结果集合
	private final GroupAllConnectionCallBackListener listener;
	private boolean responsed;

	public GroupAllConnectionRequestCallBack(final GroupAllConnectionCallBackListener listener, final CountDownLatch countDownLatch,
			final long timeout, final long timestamp, final ConcurrentHashMap<Connection, ResponseCommand> resultMap) {
		super(countDownLatch, timeout, timestamp);
		this.listener = listener;
		this.resultMap = resultMap;
		this.responsed = false;
	}

	public Map<Connection, ResponseCommand> getResultMap() {
		return this.resultMap;
	}

	@Override
	public void setException0(final Exception e, final Connection conn, final RequestCommand requestCommand) {
		if (this.resultMap
				.putIfAbsent(conn, createComunicationErrorResponseCommand(conn, e, requestCommand, conn.getRemoteSocketAddress())) == null) {
			this.countDownLatch();
		}
		this.tryNotifyListener();
	}

	@Override
	public void onResponse0(final String group, final ResponseCommand responseCommand, final Connection connection) {
		if (this.resultMap.putIfAbsent(connection, responseCommand) == null) {
			this.countDownLatch();
		}
		this.tryNotifyListener();
	}

	@Override
	public void complete() {
		this.responsed = true;
	}

	@Override
	public boolean isComplete() {
		return this.responsed;
	}

	private void tryNotifyListener() {
		if (this.tryComplete()) {
			if (this.listener != null) {
				if (this.listener.getExecutor() != null) {
					this.listener.getExecutor().execute(new Runnable() {
						public void run() {
							GroupAllConnectionRequestCallBack.this.listener.onResponse(GroupAllConnectionRequestCallBack.this.resultMap);
						}
					});
				} else {
					this.listener.onResponse(this.resultMap);
				}
			}
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		this.resultMap.clear();
	}

}