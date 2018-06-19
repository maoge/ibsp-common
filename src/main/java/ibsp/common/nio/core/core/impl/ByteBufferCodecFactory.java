package ibsp.common.nio.core.core.impl;

import ibsp.common.nio.core.buffer.IoBuffer;
import ibsp.common.nio.core.core.CodecFactory;
import ibsp.common.nio.core.core.Session;

/**
 * 编解码工厂的一个默认实现，直接发送IoBuffer
 */
public class ByteBufferCodecFactory implements CodecFactory {
	static final IoBuffer EMPTY_BUFFER = IoBuffer.allocate(0);

	private final boolean direct;

	public ByteBufferCodecFactory() {
		this(false);
	}

	public ByteBufferCodecFactory(final boolean direct) {
		super();
		this.direct = direct;
		this.encoder = new ByteBufferEncoder();
		this.decoder = new ByteBufferDecoder();
	}

	public class ByteBufferDecoder implements Decoder {

		public Object decode(final IoBuffer buff, final Session session) {
			if (buff == null) {
				return null;
			}
			if (buff.remaining() == 0) {
				return EMPTY_BUFFER;
			}
			final byte[] bytes = new byte[buff.remaining()];
			buff.get(bytes);
			final IoBuffer result = IoBuffer.allocate(bytes.length, ByteBufferCodecFactory.this.direct);
			result.put(bytes);
			result.flip();
			return result;
		}

	}

	private final Decoder decoder;

	public Decoder getDecoder() {
		return this.decoder;
	}

	public class ByteBufferEncoder implements Encoder {

		public IoBuffer encode(final Object message, final Session session) {
			final IoBuffer msgBuffer = (IoBuffer) message;
			if (msgBuffer == null) {
				return null;
			}
			if (msgBuffer.remaining() == 0) {
				return EMPTY_BUFFER;
			}
			final byte[] bytes = new byte[msgBuffer.remaining()];
			msgBuffer.get(bytes);
			final IoBuffer result = IoBuffer.allocate(bytes.length, ByteBufferCodecFactory.this.direct);
			result.put(bytes);
			result.flip();
			return result;
		}

	}

	private final Encoder encoder;

	public Encoder getEncoder() {
		return this.encoder;
	}

}