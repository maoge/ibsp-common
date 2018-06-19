package ibsp.common.nio.core.buffer;

import java.nio.ByteBuffer;

/**
 * Allocates {@link IoBuffer}s and manages them. Please implement this interface
 * if you need more advanced memory management scheme.
 * 
 * @author The Apache MINA Project (dev@mina.apache.org)
 * @version $Rev: 671827 $, $Date: 2008-06-26 10:49:48 +0200 (Thu, 26 Jun 2008)
 *          $
 */
public interface IoBufferAllocator {
	/**
	 * Returns the buffer which is capable of the specified size.
	 * 
	 * @param capacity
	 *            the capacity of the buffer
	 * @param direct
	 *            <tt>true</tt> to get a direct buffer, <tt>false</tt> to get a
	 *            heap buffer.
	 */
	IoBuffer allocate(int capacity, boolean direct);

	/**
	 * Returns the NIO buffer which is capable of the specified size.
	 * 
	 * @param capacity
	 *            the capacity of the buffer
	 * @param direct
	 *            <tt>true</tt> to get a direct buffer, <tt>false</tt> to get a
	 *            heap buffer.
	 */
	ByteBuffer allocateNioBuffer(int capacity, boolean direct);

	/**
	 * Wraps the specified NIO {@link ByteBuffer} into MINA buffer.
	 */
	IoBuffer wrap(ByteBuffer nioBuffer);

	/**
	 * Dispose of this allocator.
	 */
	void dispose();
}