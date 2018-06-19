package ibsp.common.nio.service.exception;

/**
 * Notify remoting的check异常，强制要求捕捉
 */
public class NotifyRemotingException extends Exception {

	static final long serialVersionUID = 8923187437857838L;

	public NotifyRemotingException() {
		super();
	}

	public NotifyRemotingException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotifyRemotingException(String message) {
		super(message);
	}

	public NotifyRemotingException(Throwable cause) {
		super(cause);
	}

}