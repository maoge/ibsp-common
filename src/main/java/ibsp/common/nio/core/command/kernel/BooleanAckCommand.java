package ibsp.common.nio.core.command.kernel;

import ibsp.common.nio.core.command.ResponseCommand;

/**
 * 系统级的响应消息，返回应答成功或者失败，仅是标记接口
 */
public interface BooleanAckCommand extends ResponseCommand {

	/**
	 * 获取附加错误信息
	 * 
	 * @return
	 */
	public String getErrorMsg();

	/**
	 * 设置附加错误信息
	 * 
	 * @param errorMsg
	 */
	public void setErrorMsg(String errorMsg);
}