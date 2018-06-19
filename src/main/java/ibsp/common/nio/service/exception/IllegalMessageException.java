package ibsp.common.nio.service.exception;

public class IllegalMessageException extends RuntimeException {

	static final long serialVersionUID = -1L;

	public IllegalMessageException() {
		super();

	}

	public IllegalMessageException(String message, Throwable cause) {
		super(message, cause);

	}

	public IllegalMessageException(String message) {
		super(message);

	}

	public IllegalMessageException(Throwable cause) {
		super(cause);

	}

}