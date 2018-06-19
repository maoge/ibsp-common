package ibsp.common.nio.core.util;

import java.lang.reflect.Method;

/**
 * 用于JMX注册的工具类
 */
public class MBeanUtils {
	public static void registerMBeanWithIdPrefix(final Object o, final String idPrefix) {
		boolean registered = false;
		// 优先注册到notify的MBeanServer上
		try {
			final Class<?> clazz = Class.forName(" com.taobao.notify.utils.MyMBeanServer");
			final Method getInstance = clazz.getMethod("getInstance");
			if (getInstance != null) {
				final Object mbs = getInstance.invoke(null);
				final Method registerMethod = clazz.getMethod("registerMBeanWithIdPrefix", Object.class, String.class);
				if (mbs != null && registerMethod != null) {
					registerMethod.invoke(mbs, o, idPrefix);
					registered = true;
				}
			}

		} catch (final Throwable e) {
			// ignore

		}
		if (!registered) {
			MyMBeanServer.getInstance().registerMBeanWithIdPrefix(o, idPrefix);
		}
	}
}