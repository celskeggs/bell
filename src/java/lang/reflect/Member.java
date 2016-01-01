package java.lang.reflect;

public interface Member {
	public static final int DECLARED = 1;
	public static final int PUBLIC = 0;

	Class<?> getDeclaringClass();

	String getName();

	int getModifiers();

	boolean isSynthetic();
}
