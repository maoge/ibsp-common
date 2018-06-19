package ibsp.common.nio.core.core;

import ibsp.common.nio.core.buffer.IoBuffer;

/**
 * 编解码工厂类
 */
public interface CodecFactory {

	interface Encoder {
		public IoBuffer encode(Object message, Session session);
	}

	interface Decoder {
		public Object decode(IoBuffer buff, Session session);
	}

	Encoder getEncoder();

	Decoder getDecoder();
}