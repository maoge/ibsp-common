package ibsp.common.nio.core.nio;

import java.nio.channels.FileChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.Selector;
import java.util.concurrent.Future;

import ibsp.common.nio.core.buffer.IoBuffer;
import ibsp.common.nio.core.core.EventType;
import ibsp.common.nio.core.core.Session;
import ibsp.common.nio.core.nio.impl.TimerRef;

/**
 * Nio的连接接口
 */
public interface NioSession extends Session {
	/*
	 * 派发事件
	 * 
	 * @param event
	 * 
	 * @param selector
	 */
	public void onEvent(EventType event, Selector selector);

	/**
	 * 注册读
	 * 
	 * @param selector
	 */
	public void enableRead(Selector selector);

	/**
	 * 注册写
	 * 
	 * @param selector
	 */
	public void enableWrite(Selector selector);

	/**
	 * 添加一个定时器
	 * 
	 * @param timeout
	 * @param runnable
	 * @return TODO
	 */
	public void insertTimer(TimerRef timerRef);

	/**
	 * 往该连接写入消息，可被中断，中断可能引起连接的关闭，慎重使用
	 * 
	 * @param message
	 */
	public void writeInterruptibly(Object message);

	/**
	 * 往该连接写入消息，可被中断，中断可能引起连接的关闭，慎重使用
	 * 
	 * @param message
	 */
	public Future<Boolean> asyncWriteInterruptibly(Object message);

	/**
	 * 获得连接对应的channel
	 * 
	 * @return
	 */
	public SelectableChannel channel();

	/**
	 * 从指定FileChannel的position位置开始传输size个字节到socket，返回future对象查询状态,
	 * 其中head和tail是在传输文件前后写入的数据，可以为null
	 * 
	 * @param src
	 * @param position
	 * @param size
	 * @return
	 */
	public Future<Boolean> asyncTransferFrom(IoBuffer head, IoBuffer tail, FileChannel src, long position, long size);

	/**
	 * 从指定FileChannel的position位置开始传输size个字节到socket，其中head和tail是在传输文件前后写入的数据，
	 * 可以为null
	 * 
	 * @param src
	 * @param position
	 * @param size
	 */
	public Future<Boolean> transferFrom(IoBuffer head, IoBuffer tail, FileChannel src, long position, long size);
}