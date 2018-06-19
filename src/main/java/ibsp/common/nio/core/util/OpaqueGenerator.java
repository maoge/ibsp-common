package ibsp.common.nio.core.util;

/**
 * 命令的opaque产生器
 */
public class OpaqueGenerator {
	private static int opaque = Integer.MIN_VALUE;

	// 仅用于测试
	synchronized static void setOpaque(int target) {
		opaque = target;
	}

	public static final synchronized int getNextOpaque() {
		if (opaque >= Integer.MAX_VALUE - 10) {
			resetOpaque();
		}
		return opaque++;
	}

	// 仅用于测试
	public synchronized static void resetOpaque() {
		opaque = Integer.MIN_VALUE;
	}

	// 仅用于测试
	synchronized static int getCurrentOpaque() {
		return opaque;
	}
}