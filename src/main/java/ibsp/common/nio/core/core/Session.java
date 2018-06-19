package ibsp.common.nio.core.core;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteOrder;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * 连接抽象
 */
public interface Session {

	enum SessionStatus {
		NULL, READING, WRITING, IDLE, INITIALIZE, CLOSING, CLOSED
	}

	/**
	 * Start session
	 */
	void start();

	/**
	 * Async write a message to socket,return a future
	 * 
	 * @param packet
	 * @return
	 */
	Future<Boolean> asyncWrite(Object packet);

	/**
	 * Write a message,if you don't care when the message is written
	 * 
	 * @param packet
	 */
	void write(Object packet);

	/**
	 * Check if session is closed
	 * 
	 * @return
	 */
	boolean isClosed();

	/**
	 * Close session
	 */
	void close();

	/**
	 * Return the remote end's InetSocketAddress
	 * 
	 * @return
	 */
	InetSocketAddress getRemoteSocketAddress();

	/**
	 * ��ȡ����ip��ַ
	 * 
	 * @return
	 */
	InetAddress getLocalAddress();

	/**
	 * Return true if using blocking write
	 * 
	 * @return
	 */
	boolean isUseBlockingWrite();

	/**
	 * Set if using blocking write
	 * 
	 * @param useBlockingWrite
	 */
	void setUseBlockingWrite(boolean useBlockingWrite);

	/**
	 * Return true if using blocking read
	 * 
	 * @return
	 */
	boolean isUseBlockingRead();

	void setUseBlockingRead(boolean useBlockingRead);

	/**
	 * Flush the write queue,this method may be no effect if OP_WRITE is
	 * running.
	 */
	void flush();

	/**
	 * Return true if session is expired,session is expired beacause you set the
	 * sessionTimeout,if since session's last operation form now is over this
	 * vlaue,isExpired return true,and Handler.onExpired() will be invoked.
	 * 
	 * @return
	 */
	boolean isExpired();

	/**
	 * Check if session is idle
	 * 
	 * @return
	 */
	boolean isIdle();

	/**
	 * Return current encoder
	 * 
	 * @return
	 */
	CodecFactory.Encoder getEncoder();

	/**
	 * Set encoder
	 * 
	 * @param encoder
	 */
	void setEncoder(CodecFactory.Encoder encoder);

	/**
	 * Return current decoder
	 * 
	 * @return
	 */

	CodecFactory.Decoder getDecoder();

	void setDecoder(CodecFactory.Decoder decoder);

	/**
	 * Return true if allow handling read and write concurrently,default is
	 * true.
	 * 
	 * @return
	 */
	boolean isHandleReadWriteConcurrently();

	void setHandleReadWriteConcurrently(boolean handleReadWriteConcurrently);

	/**
	 * Return the session read buffer's byte order,big end or little end.
	 * 
	 * @return
	 */
	ByteOrder getReadBufferByteOrder();

	void setReadBufferByteOrder(ByteOrder readBufferByteOrder);

	/**
	 * Set a attribute attched with this session
	 * 
	 * @param key
	 * @param value
	 */
	void setAttribute(String key, Object value);

	/**
	 * Returns all attribute key set
	 * 
	 * @return
	 */
	public Set<String> attributeKeySet();

	/**
	 * Remove attribute
	 * 
	 * @param key
	 */
	void removeAttribute(String key);

	/**
	 * Return attribute associated with key
	 * 
	 * @param key
	 * @return
	 */
	Object getAttribute(String key);

	/**
	 * Clear attributes
	 */
	void clearAttributes();

	/**
	 * Return the bytes in write queue,there bytes is in memory.Use this method
	 * to controll writing speed.
	 * 
	 * @return
	 */
	long getScheduleWritenBytes();

	/**
	 * Return last operation timestamp,operation include read,write,idle
	 * 
	 * @return
	 */
	long getLastOperationTimeStamp();

	/**
	 * return true if it is a loopback connection
	 * 
	 * @return
	 */
	boolean isLoopbackConnection();

	long getSessionIdleTimeout();

	void setSessionIdleTimeout(long sessionIdleTimeout);

	long getSessionTimeout();

	void setSessionTimeout(long sessionTimeout);

	Object setAttributeIfAbsent(String key, Object value);

	Handler getHandler();

}