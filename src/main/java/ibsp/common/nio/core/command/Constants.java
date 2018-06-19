package ibsp.common.nio.core.command;

import ibsp.common.nio.service.impl.DefaultConnection;
import ibsp.common.nio.service.impl.DefaultRemotingClient;
import ibsp.common.nio.service.impl.DefaultRemotingContext;

/**
 * 常量值
 */
public class Constants {

	/**
	 * 默认分组名
	 */
	public static final String DEFAULT_GROUP = DefaultRemotingContext.class.getSimpleName() + "_Notify_Default_Group_Name";
	// 连接数属性
	public static final String CONNECTION_COUNT_ATTR = DefaultRemotingClient.class.getName() + "_Notify_Remoting_ConnCount";

	public static final String GROUP_CONNECTION_READY_LOCK = DefaultRemotingClient.class.getName() + "_Notify_Remoting_Group_Ready_Lock";
	public static final byte TRUE = 0x01;
	public static final byte FALSE = 0x00;
	public static final String CONNECTION_ATTR = DefaultConnection.class.getName() + "_Notify_Remoting_Context";

}