package ibsp.common.nio.core.command.kernel;

import ibsp.common.nio.core.command.ResponseCommand;

/**
 * 测试请求响应命令接口
 */
public interface DummyAckCommand extends ResponseCommand {

	public String getDummy();

	public void setDummy(String dummy);
}