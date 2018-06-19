package ibsp.common.nio.core.core;

/**
 * Controller生命周期接口
 */

public interface ControllerLifeCycle {

	void notifyReady();

	void notifyStarted();

	void notifyAllSessionClosed();

	void notifyException(Throwable t);

	void notifyStopped();
}