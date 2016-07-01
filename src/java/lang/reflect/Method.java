package java.lang.reflect;

import java.util.Arrays;

import com.celskeggs.bell.support.IncompleteImplementationError;

public final class Method extends Executable {
	private final Class<?> parent;
	private final String name;
	private final int modifiers;
	private final Class<?>[] parameters;
	private final Class<?>[] exceptions;
	private final Class<?> returnType;

	private Method(Class<?> parent, String name, int mod, Class<?>[] parameters, Class<?> returnType,
			Class<?>[] exceptions) {
		this.parent = parent;
		this.name = name;
		this.modifiers = mod;
		this.parameters = parameters;
		this.returnType = returnType;
		this.exceptions = exceptions;
	}

	public Class<?> getDeclaringClass() {
		return parent;
	}

	public String getName() {
		return name;
	}

	private static final int MODIFIERS_ALLOWED = Modifier.methodModifiers();

	public int getModifiers() {
		return modifiers & MODIFIERS_ALLOWED;
	}

	@Override
	int getAllModifiers() {
		return modifiers;
	}

	public Class<?> getReturnType() {
		return returnType;
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
		if (obj instanceof Method) {
			Method m = (Method) obj;
			return parent == m.parent && name.equals(m.name) && Arrays.equals(parameters, m.parameters)
					&& returnType == m.returnType;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return parent.getName().hashCode() ^ name.hashCode();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(Modifier.toString(getModifiers()));
		if (sb.length() != 0) {
			sb.append(' ');
		}
		sb.append(returnType.getName()).append(' ');
		sb.append(parent.getName()).append('.');
		sb.append(name).append('(');
		if (parameters.length != 0) {
			// TODO: check that this is correct
			sb.append(parameters[0].getName());
			for (int i = 1; i < parameters.length; i++) {
				sb.append(',').append(parameters[i].getName());
			}
		}
		sb.append(')');
		if (exceptions.length != 0) {
			sb.append(" throws ").append(exceptions[0].getName());
			for (int i = 1; i < exceptions.length; i++) {
				sb.append(',').append(exceptions[i].getName());
			}
		}
		return sb.toString();
	}

	public Object invoke(Object obj, Object... args)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		throw new IncompleteImplementationError();
	}

	private static final int ACC_BRIDGE = 0x0040;

	public boolean isBridge() {
		return (modifiers & ACC_BRIDGE) != 0;
	}

	public boolean isDefault() {
		// TODO: check this
		return Modifier.isPublic(modifiers) && !Modifier.isAbstract(modifiers) && !Modifier.isStatic(modifiers)
				&& parent.isInterface();
	}
}
