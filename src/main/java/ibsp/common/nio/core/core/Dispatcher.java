package ibsp.common.nio.core.core;

/**
 * 任务派发器
 */
public interface Dispatcher {
	public void dispatch(Runnable r);

	public void stop();
}