package ibsp.common.nio.core.command;

/**
 * 应答状态
 */
public enum ResponseStatus {
	NO_ERROR(null), // 正常成功
	ERROR("Error by user"), // 错误，响应端主动设置
	EXCEPTION("Exception occured"), // 异常
	UNKNOWN("Unknow error"), // 没有注册Listener，包括CheckMessageListener和MessageListener
	THREADPOOL_BUSY("Thread pool is busy"), // 响应段线程繁忙
	ERROR_COMM("Communication error"), // 通讯错误，如编码错误
	NO_PROCESSOR("There is no processor to handle this request"), // 没有该请求命令的处理器
	TIMEOUT("Operation timeout"); // 响应超时

	private String errorMessage;

	private ResponseStatus(final String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}

}