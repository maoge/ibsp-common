package ibsp.common.nio.core.command;

import java.net.InetSocketAddress;

/**
 * 应答命令的抽象基础类，提供一些基础属性，应答命令可以直接继承此命令
 */
public abstract class AbstractResponseCommand implements ResponseCommand {

	static final long serialVersionUID = -1L;

	protected ResponseStatus responseStatus;
	protected InetSocketAddress responseHost;
	protected Integer opaque;
	private long responseTime;

	public ResponseStatus getResponseStatus() {
		return this.responseStatus;
	}

	public void setResponseStatus(final ResponseStatus responseStatus) {
		this.responseStatus = responseStatus;

	}

	public boolean isBoolean() {

		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.opaque == null ? 0 : this.opaque.hashCode());
		result = prime * result + (this.responseHost == null ? 0 : this.responseHost.hashCode());
		result = prime * result + (this.responseStatus == null ? 0 : this.responseStatus.hashCode());
		result = prime * result + (int) (this.responseTime ^ this.responseTime >>> 32);
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final AbstractResponseCommand other = (AbstractResponseCommand) obj;
		if (this.opaque == null) {
			if (other.opaque != null) {
				return false;
			}
		} else if (!this.opaque.equals(other.opaque)) {
			return false;
		}
		if (this.responseHost == null) {
			if (other.responseHost != null) {
				return false;
			}
		} else if (!this.responseHost.equals(other.responseHost)) {
			return false;
		}
		if (this.responseStatus != other.responseStatus) {
			return false;
		}
		if (this.responseTime != other.responseTime) {
			return false;
		}
		return true;
	}

	public InetSocketAddress getResponseHost() {
		return this.responseHost;
	}

	public void setResponseHost(final InetSocketAddress address) {
		this.responseHost = address;
	}

	public long getResponseTime() {
		return this.responseTime;
	}

	public void setResponseTime(final long time) {
		this.responseTime = time;
	}

	public Integer getOpaque() {
		return this.opaque;
	}

	public void setOpaque(final Integer opaque) {
		this.opaque = opaque;
	}

}