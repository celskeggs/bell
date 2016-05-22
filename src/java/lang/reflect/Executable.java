package java.lang.reflect;

public abstract class Executable extends AccessibleObject
		implements Member /* , GenericDeclaration */ {
	Executable() {
	}

	public abstract Class<?> getDeclaringClass();

	public abstract String getName();

	public abstract int getModifiers();

	public abstract Class<?>[] getParameterTypes();

	public abstract int getParameterCount();

	abstract int getAllModifiers();

	public abstract Class<?>[] getExceptionTypes();

	private static final int ACC_VARARGS = 0x0080;

	public boolean isVarArgs() {
		return (getAllModifiers() & ACC_VARARGS) != 0;
	}

	private static final int ACC_SYNTHETIC = 0x1000;

	public boolean isSynthetic() {
		return (getAllModifiers() & ACC_SYNTHETIC) != 0;
	}
}
