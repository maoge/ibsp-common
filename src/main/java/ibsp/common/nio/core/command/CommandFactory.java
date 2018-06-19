package ibsp.common.nio.core.command;

import ibsp.common.nio.core.command.kernel.BooleanAckCommand;
import ibsp.common.nio.core.command.kernel.HeartBeatRequestCommand;

/**
 * 协议工厂类，任何协议的实现都必须实现此工厂接口，提供创建BooleanAckCommand和HeartBeatRequestCommand的方法
 */
public interface CommandFactory {
	/**
	 * 创建特定于协议的BooleanAckCommand
	 * 
	 * @param request
	 *            请求头
	 * @param responseStatus
	 *            响应状态
	 * @param errorMsg
	 *            错误信息
	 * @return
	 */
	public BooleanAckCommand createBooleanAckCommand(CommandHeader request, ResponseStatus responseStatus, String errorMsg);

	/**
	 * 创建特定于协议的心跳命令
	 * 
	 * @return
	 */
	public HeartBeatRequestCommand createHeartBeatCommand();

}