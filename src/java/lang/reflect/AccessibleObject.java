package java.lang.reflect;

public class AccessibleObject /* implements AnnotatedElement */ {
	private boolean accessible;

	protected AccessibleObject() {
	}

	public static void setAccessible(AccessibleObject[] array,
			boolean flag) /* throws SecurityException */ {
		for (AccessibleObject o : array) {
			o.setAccessible(flag);
		}
	}

	public void setAccessible(boolean flag) /* throws SecurityException */ {
		// TODO: refuse, in some cases?
		this.accessible = flag;
	}

	public boolean isAccessible() {
		return accessible;
	}
}
