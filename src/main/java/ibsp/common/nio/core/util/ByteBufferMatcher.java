package ibsp.common.nio.core.util;

import java.util.List;

import ibsp.common.nio.core.buffer.IoBuffer;

/**
 * ByteBuffer匹配器
 */
public interface ByteBufferMatcher {

	public int matchFirst(IoBuffer buffer);

	public List<Integer> matchAll(final IoBuffer buffer);

}