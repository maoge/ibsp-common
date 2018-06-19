package ibsp.common.nio.service.config;

import java.util.HashMap;
import java.util.Map;

import ibsp.common.nio.core.command.CommandFactory;
import ibsp.common.nio.core.core.CodecFactory;

/**
 * wire协议类型，任何想要使用gecko的协议都需要继承此类并实现相应方法
 */

public abstract class WireFormatType {
	private static Map<String, WireFormatType> registeredWireFormatType = new HashMap<String, WireFormatType>();

	/**
	 * 注册协议类型
	 * 
	 * @param wireFormatType
	 */
	public static void registerWireFormatType(final WireFormatType wireFormatType) {
		if (wireFormatType == null) {
			throw new IllegalArgumentException("Null wire format");
		}
		registeredWireFormatType.put(wireFormatType.name(), wireFormatType);
	}

	/**
	 * 取消协议类型的注册
	 * 
	 * @param wireFormatType
	 */
	public static void unregisterWireFormatType(final WireFormatType wireFormatType) {
		if (wireFormatType == null) {
			throw new IllegalArgumentException("Null wire format");
		}
		registeredWireFormatType.remove(wireFormatType.name());
	}

	@Override
	public String toString() {
		return this.name();
	}

	public static WireFormatType valueOf(final String name) {
		return registeredWireFormatType.get(name);

	}

	/**
	 * 协议的scheme
	 * 
	 * @return
	 */
	public abstract String getScheme();

	/**
	 * 协议的编解码工厂
	 * 
	 * @return
	 */
	public abstract CodecFactory newCodecFactory();

	/**
	 * 协议的命令工厂
	 * 
	 * @return
	 */
	public abstract CommandFactory newCommandFactory();

	/**
	 * 协议名称
	 * 
	 * @return
	 */
	public abstract String name();
}