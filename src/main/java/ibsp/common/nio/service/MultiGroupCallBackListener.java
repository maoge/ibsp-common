package ibsp.common.nio.service;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import ibsp.common.nio.core.command.ResponseCommand;

/**
 * 多分组请求的应答监听器
 */

public interface MultiGroupCallBackListener {

	public void onResponse(Map<String/* group */, ResponseCommand> groupResponses, Object... args);

	public ThreadPoolExecutor getExecutor();
}