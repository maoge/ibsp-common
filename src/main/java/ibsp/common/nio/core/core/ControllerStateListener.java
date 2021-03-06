package ibsp.common.nio.core.core;

/**
 * Controller生命周期监听器
 */
public interface ControllerStateListener {

	void onStarted(final Controller controller);

	void onReady(final Controller controller);

	void onAllSessionClosed(final Controller controller);

	void onStopped(final Controller controller);

	void onException(final Controller controller, Throwable t);
}