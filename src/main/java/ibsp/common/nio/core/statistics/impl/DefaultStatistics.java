package ibsp.common.nio.core.statistics.impl;

import ibsp.common.nio.core.statistics.Statistics;

/**
 * 默认统计类
 */
public class DefaultStatistics implements Statistics {
	public void start() {

	}

	public double getSendBytesPerSecond() {
		return 0;
	}

	public double getReceiveBytesPerSecond() {
		return 0;
	}

	public boolean isStatistics() {
		return false;
	}

	public long getStartedTime() {
		return 0;
	}

	public void reset() {

	}

	public void restart() {

	}

	public double getProcessedMessageAverageTime() {
		return 0;
	}

	public long getProcessedMessageCount() {
		return 0;
	}

	public void statisticsProcess(final long n) {

	}

	public void stop() {

	}

	public long getRecvMessageCount() {

		return 0;
	}

	public long getRecvMessageTotalSize() {

		return 0;
	}

	public long getRecvMessageAverageSize() {

		return 0;
	}

	public double getRecvMessageCountPerSecond() {

		return 0;
	}

	public long getWriteMessageCount() {

		return 0;
	}

	public long getWriteMessageTotalSize() {

		return 0;
	}

	public long getWriteMessageAverageSize() {

		return 0;
	}

	public void statisticsRead(final long n) {

	}

	public void statisticsWrite(final long n) {

	}

	public double getWriteMessageCountPerSecond() {

		return 0;
	}

	public double getAcceptCountPerSecond() {
		return 0;
	}

	public void statisticsAccept() {

	}

	public void setReceiveThroughputLimit(final double receivePacketRate) {
	}

	public boolean isReceiveOverFlow() {
		return false;
	}

	public boolean isSendOverFlow() {
		return false;
	}

	public double getSendThroughputLimit() {
		return -1.0;
	}

	public void setSendThroughputLimit(final double sendThroughputLimit) {
	}

	public final double getReceiveThroughputLimit() {
		return -1.0;
	}

}