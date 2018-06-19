package ibsp.common.nio.core.command;

import java.net.InetSocketAddress;

/**
 * 应答命令公共接口
 */
public interface ResponseCommand extends CommandHeader {
	static final long serialVersionUID = 77788812547386438L;

	/**
	 * 返回响应状态
	 * 
	 * @return
	 */
	public ResponseStatus getResponseStatus();

	/**
	 * 设置响应状态
	 * 
	 * @param responseStatus
	 */
	public void setResponseStatus(ResponseStatus responseStatus);

	/**
	 * 是否为BooleanAckCommand
	 * 
	 * @return
	 */
	public boolean isBoolean();

	/**
	 * 返回响应的远端地址
	 * 
	 * @return
	 */
	public InetSocketAddress getResponseHost();

	/**
	 * 设置响应的远端地址
	 * 
	 * @param address
	 */
	public void setResponseHost(InetSocketAddress address);

	/**
	 * 返回响应的时间戳
	 * 
	 * @return
	 */
	public long getResponseTime();

	/**
	 * 设置响应时间戳
	 * 
	 * @param time
	 */
	public void setResponseTime(long time);

	/**
	 * 设置响应的opaque
	 * 
	 * @param opaque
	 */
	public void setOpaque(Integer opaque);
}