package ibsp.common.nio.service.processor;

import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ibsp.common.nio.core.command.ResponseStatus;
import ibsp.common.nio.core.command.kernel.HeartBeatRequestCommand;
import ibsp.common.nio.core.util.RemotingUtils;
import ibsp.common.nio.service.Connection;
import ibsp.common.nio.service.RequestProcessor;
import ibsp.common.nio.service.exception.NotifyRemotingException;

/**
 * 心跳命令的处理器
 */

public class HeartBeatCommandProecssor implements RequestProcessor<HeartBeatRequestCommand> {

	private static Logger logger = LoggerFactory.getLogger(HeartBeatCommandProecssor.class);

	public HeartBeatCommandProecssor() {
		super();
	}

	public void handleRequest(final HeartBeatRequestCommand request, final Connection conn) {
		try {
			conn.response(conn.getRemotingContext().getCommandFactory()
					.createBooleanAckCommand(request.getRequestHeader(), ResponseStatus.NO_ERROR, null));
		} catch (final NotifyRemotingException e) {
			logger.error("发送心跳应答给连接[{}]失败{}", RemotingUtils.getAddrString(conn.getRemoteSocketAddress()), e.getMessage());
		}
	}

	public ThreadPoolExecutor getExecutor() {
		return null;
	}

}