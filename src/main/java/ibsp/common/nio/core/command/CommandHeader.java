package ibsp.common.nio.core.command;

/**
 * 存放在callBack中的请求信息，出于节省内存考虑，最好不要存放协议体
 */
public interface CommandHeader extends Command {
	/**
	 * 返回请求的opaque
	 * 
	 * @return
	 */
	public Integer getOpaque();

}