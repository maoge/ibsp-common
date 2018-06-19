package ibsp.common.nio.service.config;

public class ClientConfig extends BaseConfig {

	public ClientConfig() {
		super();
		// 设置判断连接空闲时间为10秒
		this.setIdleTime(10);
		this.setMaxCallBackCount(100000);
		this.setSelectorPoolSize(Runtime.getRuntime().availableProcessors());
		this.setReadThreadCount(0);
		this.setMaxScheduleWrittenBytes(Runtime.getRuntime().maxMemory() / 10);
	}

	/**
	 * 连接超时,单位毫秒
	 */
	private long connectTimeout = 80000L;
	/**
	 * 重连间隔，单位毫秒
	 */
	private long healConnectionInterval = 2000L;

	/**
	 * 重连管理器的连接池大小
	 */
	private int healConnectionExecutorPoolSize = 1;

	public long getConnectTimeout() {
		return this.connectTimeout;
	}

	public void setConnectTimeout(final long connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getHealConnectionExecutorPoolSize() {
		return this.healConnectionExecutorPoolSize;
	}

	public void setHealConnectionExecutorPoolSize(final int healConnectionExecutorPoolSize) {
		this.healConnectionExecutorPoolSize = healConnectionExecutorPoolSize;
	}

	public long getHealConnectionInterval() {
		return this.healConnectionInterval;
	}

	public void setHealConnectionInterval(final long healConnectionInterval) {
		this.healConnectionInterval = healConnectionInterval;
	}

}