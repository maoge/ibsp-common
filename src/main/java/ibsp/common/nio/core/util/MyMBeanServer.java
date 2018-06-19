package ibsp.common.nio.core.util;

import java.lang.management.ManagementFactory;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public final class MyMBeanServer {

	private MBeanServer mbs = null;

	private final ConcurrentHashMap<String, ConcurrentHashMap<String, AtomicLong>> idMap = new ConcurrentHashMap<String, ConcurrentHashMap<String, AtomicLong>>();
	private final ReentrantLock lock = new ReentrantLock();

	private static class Holder {
		private static final MyMBeanServer instance = new MyMBeanServer();
	}

	// private static MyMBeanServer me = new MyMBeanServer();

	private MyMBeanServer() {
		this.mbs = ManagementFactory.getPlatformMBeanServer();
	}

	public static MyMBeanServer getInstance() {
		return Holder.instance;
	}

	public void registMBean(final Object o, final String name) {
		// 注册MBean
		if (null != this.mbs) {
			try {
				this.mbs.registerMBean(o, new ObjectName(o.getClass().getPackage().getName() + ":type=" + o.getClass().getSimpleName()
						+ (null == name ? ",id=" + o.hashCode() : ",name=" + name + "-" + o.hashCode())));
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void registerMBeanWithId(final Object o, final String id) {
		// 注册MBean
		if (null == id || id.length() == 0) {
			throw new IllegalArgumentException("must set id");
		}
		if (null != this.mbs) {
			try {
				this.mbs.registerMBean(o, new ObjectName(o.getClass().getPackage().getName() + ":type=" + o.getClass().getSimpleName()
						+ ",id=" + id));
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private String getId(final String name, final String idPrefix) {
		ConcurrentHashMap<String, AtomicLong> subMap = this.idMap.get(name);
		if (null == subMap) {
			this.lock.lock();
			try {
				subMap = this.idMap.get(name);
				if (null == subMap) {
					subMap = new ConcurrentHashMap<String, AtomicLong>();
					this.idMap.put(name, subMap);
				}
			} finally {
				this.lock.unlock();
			}
		}

		AtomicLong indexValue = subMap.get(idPrefix);
		if (null == indexValue) {
			this.lock.lock();
			try {
				indexValue = subMap.get(idPrefix);
				if (null == indexValue) {
					indexValue = new AtomicLong(0);
					subMap.put(idPrefix, indexValue);
				}
			} finally {
				this.lock.unlock();
			}
		}
		final long value = indexValue.incrementAndGet();
		final String result = idPrefix + "-" + value;
		return result;
	}

	public void registerMBeanWithIdPrefix(final Object o, String idPrefix) {
		// 注册MBean
		if (null != this.mbs) {
			if (null == idPrefix || idPrefix.length() == 0) {
				idPrefix = "default";
			}
			idPrefix = idPrefix.replace(":", "-");

			try {
				final String id = this.getId(o.getClass().getName(), idPrefix);

				this.mbs.registerMBean(o, new ObjectName(o.getClass().getPackage().getName() + ":type=" + o.getClass().getSimpleName()
						+ ",id=" + id));
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}