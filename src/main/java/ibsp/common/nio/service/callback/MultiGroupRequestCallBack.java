package ibsp.common.nio.service.callback;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import ibsp.common.nio.core.command.RequestCommand;
import ibsp.common.nio.core.command.ResponseCommand;
import ibsp.common.nio.service.Connection;
import ibsp.common.nio.service.MultiGroupCallBackListener;
import ibsp.common.nio.service.impl.DefaultConnection;

/**
 * 发送给多个分组的请求回调
 */

public class MultiGroupRequestCallBack extends AbstractRequestCallBack {
	private final ConcurrentHashMap<String/* group */, ResponseCommand/* 应答 */> responseCommandMap;
	private final MultiGroupCallBackListener listener;
	private final AtomicBoolean responsed;
	private final Object[] args; // 客户传入的附加参数

	public MultiGroupRequestCallBack(final MultiGroupCallBackListener listener, final CountDownLatch countDownLatch, final long timeout,
			final long timestamp, final ConcurrentHashMap<String/* group */, ResponseCommand/* 应答 */> responseCommandMap,
			final AtomicBoolean responsed, final Object... args) {
		super(countDownLatch, timeout, timestamp);
		this.listener = listener;
		this.responseCommandMap = responseCommandMap;
		this.responsed = responsed;
		this.args = args;
	}

	@Override
	public void setException0(final Exception e, final Connection conn, final RequestCommand requestCommand) {
		final String group = this.getGroupFromConnection(conn, requestCommand);
		if (group != null) {
			if (this.responseCommandMap.putIfAbsent(group,
					createComunicationErrorResponseCommand(conn, e, requestCommand, conn.getRemoteSocketAddress())) == null) {
				this.countDownLatch();
			}
		}
		this.tryNotifyListener();
	}

	private String getGroupFromConnection(final Connection conn, final RequestCommand requestCommand) {
		String group = null;
		if (conn != null) {
			group = ((DefaultConnection) conn).removeOpaqueToGroupMapping(requestCommand.getOpaque());
		}
		return group;
	}

	@Override
	public void complete() {
		this.responsed.set(true);
	}

	@Override
	public boolean isComplete() {
		return this.responsed.get();
	}

	@Override
	public void onResponse0(String group, final ResponseCommand responseCommand, final Connection connection) {
		final DefaultConnection defaultConnection = (DefaultConnection) connection;
		if (defaultConnection != null) {
			// 删除opaque到group的映射
			final String reqGroup = defaultConnection.removeOpaqueToGroupMapping(responseCommand.getOpaque());
			if (reqGroup != null) {
				group = reqGroup;
			}
		}
		if (group != null) {
			if (this.responseCommandMap.putIfAbsent(group, responseCommand) == null) {
				this.countDownLatch();
			}
			this.tryNotifyListener();
		} else {
			// 分析来说不应该出现这种情况，但是预防万一，还是要确保移除callBack
			if (defaultConnection != null) {
				defaultConnection.removeRequestCallBack(responseCommand.getOpaque());
			}
		}
	}

	private void tryNotifyListener() {
		if (this.tryComplete()) {
			if (this.listener != null) {
				if (this.listener.getExecutor() != null) {
					this.listener.getExecutor().execute(new Runnable() {
						public void run() {
							MultiGroupRequestCallBack.this.listener.onResponse(MultiGroupRequestCallBack.this.responseCommandMap,
									MultiGroupRequestCallBack.this.args);
						}
					});
				} else {
					this.listener.onResponse(this.responseCommandMap, this.args);
				}
			}
		}
	}

	public Map<String, ResponseCommand> getResponseCommandMap() {
		return this.responseCommandMap;
	}

}