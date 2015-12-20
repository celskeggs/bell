package java.lang;

public class Error extends Throwable {

    public Error() {
        super();
    }

    public Error(String message) {
        super(message);
    }

	public Error(String message, Throwable cause) {
		super(message, cause);
	}

	protected Error(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public Error(Throwable cause) {
		super(cause);
	}
}
