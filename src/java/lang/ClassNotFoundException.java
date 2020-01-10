package java.lang;

public class ClassNotFoundException extends Exception {

	public ClassNotFoundException() {
		super();
	}

	public ClassNotFoundException(String message) {
		super(message);
	}

    public ClassNotFoundException(String message, Throwable ex) {
        super(message, ex);
    }
}
