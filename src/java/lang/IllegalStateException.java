package java.lang;

public class IllegalStateException extends RuntimeException {

	public IllegalStateException() {
	}

	public IllegalStateException(String message) {
		super(message);
	}

	public IllegalStateException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalStateException(Throwable cause) {
		super(cause);
	}
}
