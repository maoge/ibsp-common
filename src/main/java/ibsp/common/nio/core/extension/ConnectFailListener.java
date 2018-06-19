package ibsp.common.nio.core.extension;

/**
 * 扩展监听器，监听连接失败事件
 */
public interface ConnectFailListener {

	public void onConnectFail(Object... args);
}