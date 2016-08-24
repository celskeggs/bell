package java.lang.reflect;

import com.celskeggs.bell.support.IncompleteImplementationError;

public final class Array {
	public static Object newInstance(Class<?> componentType, int length) {
		throw new IncompleteImplementationError();
	}
	
	public static int getLength(Object array) {
		throw new IncompleteImplementationError();
	}
}
