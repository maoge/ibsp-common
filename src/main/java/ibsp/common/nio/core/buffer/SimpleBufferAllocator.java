package ibsp.common.nio.core.buffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * A simplistic {@link IoBufferAllocator} which simply allocates a new buffer
 * every time.
 * 
 * @author The Apache MINA Project (dev@mina.apache.org)
 * @version $Rev: 671827 $, $Date: 2008-06-26 10:49:48 +0200 (Thu, 26 Jun 2008)
 *          $
 */
public class SimpleBufferAllocator implements IoBufferAllocator {

	public IoBuffer allocate(int capacity, boolean direct) {
		return wrap(allocateNioBuffer(capacity, direct));
	}

	public ByteBuffer allocateNioBuffer(int capacity, boolean direct) {
		ByteBuffer nioBuffer;
		if (direct) {
			nioBuffer = ByteBuffer.allocateDirect(capacity);
		} else {
			nioBuffer = ByteBuffer.allocate(capacity);
		}
		return nioBuffer;
	}

	public IoBuffer wrap(ByteBuffer nioBuffer) {
		return new SimpleBuffer(nioBuffer);
	}

	public void dispose() {
	}

	private class SimpleBuffer extends AbstractIoBuffer {
		private ByteBuffer buf;

		protected SimpleBuffer(ByteBuffer buf) {
			super(SimpleBufferAllocator.this, buf.capacity());
			this.buf = buf;
			buf.order(ByteOrder.BIG_ENDIAN);
		}

		protected SimpleBuffer(SimpleBuffer parent, ByteBuffer buf) {
			super(parent);
			this.buf = buf;
		}

		@Override
		public ByteBuffer buf() {
			return buf;
		}

		@Override
		protected void buf(ByteBuffer buf) {
			this.buf = buf;
		}

		@Override
		protected IoBuffer duplicate0() {
			return new SimpleBuffer(this, this.buf.duplicate());
		}

		@Override
		protected IoBuffer slice0() {
			return new SimpleBuffer(this, this.buf.slice());
		}

		@Override
		protected IoBuffer asReadOnlyBuffer0() {
			return new SimpleBuffer(this, this.buf.asReadOnlyBuffer());
		}

		@Override
		public byte[] array() {
			return buf.array();
		}

		@Override
		public int arrayOffset() {
			return buf.arrayOffset();
		}

		@Override
		public boolean hasArray() {
			return buf.hasArray();
		}

		@Override
		public void free() {
		}
	}
}