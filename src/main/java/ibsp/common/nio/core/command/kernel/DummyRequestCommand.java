package ibsp.common.nio.core.command.kernel;

import ibsp.common.nio.core.command.RequestCommand;

/**
 * 用于测试的请求命令接口
 */
public interface DummyRequestCommand extends RequestCommand {

	/**
	 * 附加字符串信息
	 * 
	 * @return
	 */
	public String getDummy();

	public void setDummy(String dummy);
}