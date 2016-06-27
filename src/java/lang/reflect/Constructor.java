package java.lang.reflect;

import java.util.Arrays;

import com.celskeggs.support.IncompleteImplementationError;

public final class Constructor<T> extends Executable {
	private final Class<T> parent;
	private final int modifiers;
	private final Class<?>[] parameters;
	private final Class<?>[] exceptions;

	private Constructor(Class<T> parent, int mod, Class<?>[] parameters, Class<?>[] exceptions) {
		this.parent = parent;
		this.modifiers = mod;
		this.parameters = parameters;
		this.exceptions = exceptions;
	}

	public Class<?> getDeclaringClass() {
		return parent;
	}

	public String getName() {
		return parent.getName();
	}

	private static final int MODIFIERS_ALLOWED = Modifier.constructorModifiers();

	public int getModifiers() {
		return modifiers & MODIFIERS_ALLOWED;
	}

	@Override
	int getAllModifiers() {
		return modifiers;
	}

	@Override
	public Class<?>[] getParameterTypes() {
		return Arrays.copyOf(parameters, parameters.length);
	}

	@Override
	public int getParameterCount() {
		return parameters.length;
	}

	public Class<?>[] getExceptionTypes() {
		return Arrays.copyOf(exceptions, exceptions.length);
	}

	public boolean equals(Object obj) {
		if (obj instanceof Constructor) {
			Constructor<?> c = (Constructor<?>) obj;
			return parent == c.parent && Arrays.equals(parameters, c.parameters);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return parent.getName().hashCode();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(Modifier.toString(getModifiers()));
		if (sb.length() != 0) {
			sb.append(' ');
		}
		sb.append(parent.getName()).append('(');
		if (parameters.length != 0) {
			// TODO: check that this is correct
			sb.append(parameters[0].getName());
			for (int i = 1; i < parameters.length; i++) {
				sb.append(',').append(parameters[i].getName());
			}
		}
		sb.append(')');
		return sb.toString();
	}

	public T newInstance(Object... initargs)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		throw new IncompleteImplementationError();
	}
}
