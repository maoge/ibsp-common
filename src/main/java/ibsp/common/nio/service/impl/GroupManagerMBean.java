package ibsp.common.nio.service.impl;

import java.util.Map;
import java.util.Set;

/**
 * 分组连接管理器的MBean
 */

public interface GroupManagerMBean {
	/**
	 * 获取分组对应的连接信息映射
	 * 
	 * @return
	 */
	Map<String, Set<String>> getGroupConnectionInfo();
}