package ibsp.common.nio.core.util;

import java.util.ArrayList;
import java.util.List;

import ibsp.common.nio.core.buffer.IoBuffer;

/**
 * Shift or算法的匹配实现
 */
public class ShiftOrByteBufferMatcher implements ByteBufferMatcher {

	private int[] b;
	private int lim;

	private final int patternLen;

	public ShiftOrByteBufferMatcher(final IoBuffer pat) {
		if (pat == null || pat.remaining() == 0) {
			throw new IllegalArgumentException("blank buffer");
		}
		this.patternLen = pat.remaining();
		this.preprocess(pat);
	}

	/**
	 * 预处理
	 * 
	 * @param pat
	 */
	private void preprocess(final IoBuffer pat) {
		this.b = new int[256];
		this.lim = 0;
		for (int i = 0; i < 256; i++) {
			this.b[i] = ~0;

		}
		for (int i = 0, j = 1; i < this.patternLen; i++, j <<= 1) {
			this.b[ByteBufferUtils.uByte(pat.get(i))] &= ~j;
			this.lim |= j;
		}
		this.lim = ~(this.lim >> 1);

	}

	public final List<Integer> matchAll(final IoBuffer buffer) {
		final List<Integer> matches = new ArrayList<Integer>();
		final int bufferLimit = buffer.limit();
		int state = ~0;
		for (int pos = buffer.position(); pos < bufferLimit; pos++) {
			state <<= 1;
			state |= this.b[ByteBufferUtils.uByte(buffer.get(pos))];
			if (state < this.lim) {
				matches.add(pos - this.patternLen + 1);
			}
		}
		return matches;
	}

	public final int matchFirst(final IoBuffer buffer) {
		if (buffer == null) {
			return -1;
		}
		final int bufferLimit = buffer.limit();
		int state = ~0;
		for (int pos = buffer.position(); pos < bufferLimit; pos++) {
			state = (state <<= 1) | this.b[ByteBufferUtils.uByte(buffer.get(pos))];
			if (state < this.lim) {
				return pos - this.patternLen + 1;
			}
		}
		return -1;
	}

}