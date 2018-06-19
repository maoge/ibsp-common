package ibsp.common.nio.core.command;

/**
 * 请求命令公共接口
 */
public interface RequestCommand extends CommandHeader {

	/**
	 * 返回请求的头部，用于保存在callBack中
	 * 
	 * @return
	 */
	public CommandHeader getRequestHeader();
}