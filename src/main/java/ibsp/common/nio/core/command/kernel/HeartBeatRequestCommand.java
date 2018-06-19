package ibsp.common.nio.core.command.kernel;

import ibsp.common.nio.core.command.RequestCommand;

/**
 * 心跳请求命令，仅是一个标记接口，用户需要实现此接口并实现相应的CommandFactory
 */
public interface HeartBeatRequestCommand extends RequestCommand {

}