package java.lang.reflect;

public class InvocationTargetException extends ReflectiveOperationException {

	protected InvocationTargetException() {
		super();
	}

	public InvocationTargetException(Throwable target) {
		super(target);
	}

	public InvocationTargetException(Throwable target, String detail) {
		super(detail, target);
	}

	public Throwable getTargetException() {
		return getCause();
	}
}
