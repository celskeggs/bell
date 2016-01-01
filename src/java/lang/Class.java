package java.lang;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import vm.VMAccess;
import vm.VMClass;

public final class Class<T> /* implements Serializable, GenericDeclaration, Type, AnnotatedElement */ {

	private final VMClass cls;

	private Class(VMClass cls) {
		this.cls = cls;
	}

	public String toString() {
		if (cls.isPrimitive()) {
			return getName();
		} else {
			if (isInterface()) {
				return "interface " + getName();
			} else {
				return "class " + getName();
			}
		}
	}

	public static Class<?> forName(String className) throws ClassNotFoundException {
		return Class.forName(className, true, getCallerClassLoader(1));
	}

	private static ClassLoader getCallerClassLoader(int who) {
		throw new IncompleteImplementationError();
	}

	public static Class<?> forName(String className, boolean initialize, ClassLoader loader) {
		if (className.indexOf('/') != -1) {
			throw new ClassNotFoundException();
		}
		if (className.endsWith("[]")) {
			return forName(className.substring(0, className.length() - 2), false, loader).getEnclosingArray();
		}
		if (loader == null) {
			loader = ClassLoader.getBootstrapClassLoader();
		}
		Class<?> cls = loader.loadClass(className);
		if (initialize) {
			cls.ensureInitialized();
		}
		return cls;
	}

	public T newInstance() throws InstantiationException, IllegalAccessException {
		return (T) cls.newInstance();
	}

	public boolean isInstance(Object o) {
		return o != null && cls.isAssignableFrom(o.getClass().cls);
	}

	public boolean isAssignableFrom(Class<?> o) {
		return cls.isAssignableFrom(o.cls);
	}

	public boolean isInterface() {
		return cls.isInterface();
	}

	public boolean isArray() {
		return cls.isArray();
	}

	public boolean isPrimitive() {
		return cls.isPrimitive();
	}

	public boolean isAnnotation() {
		return isInterface() && java.lang.annotation.Annotation.class.isAssignableFrom(this);
	}

	public boolean isSynthetic() {
		return cls.isSynthetic();
	}

	public String getName() {
		// TODO: review
		return cls.getName().replace('/', '.');
	}

	public ClassLoader getClassLoader() {
		return cls.getClassLoader();
	}

	public Class<? super T> getSuperclass() {
		return (Class<? super T>) cls.getSuperClass().getRealClass();
	}

	// public Type getGenericSuperclass() {}

	// public Package getPackage() {}

	public Class<?>[] getInterfaces() {
		Class<?>[] interfaces = new Class<?>[cls.getInterfaceCount()];
		for (int i = 0; i < interfaces.length; i++) {
			interfaces[i] = cls.getInterfaceN(i).getRealClass();
		}
		return interfaces;
	}

	// public Type[] getGenericInterfaces() {}

	public Class<?> getComponentType() {
		return cls.getComponentType();
	}

	public int getModifiers() {
		return cls.getModifiers();
	}

	public Object[] getSigners() {
		return null;
	}

	public Method getEnclosingMethod() {
		throw new IncompleteImplementationError();
	}

	public Constructor<?> getEnclosingConstructor() {
		throw new IncompleteImplementationError();
	}

	public Class<?> getDeclaringClass() {
		throw new IncompleteImplementationError();
	}

	public Class<?> getEnclosingClass() {
		throw new IncompleteImplementationError();
	}

	public String getSimpleName() {
		String name = getName();
		// TODO: check this behavior
		return name.substring(name.lastIndexOf('.') + 1);
	}

	public String getCanonicalName() {
		throw new IncompleteImplementationError();
	}

	public boolean isAnonymousClass() {
		throw new IncompleteImplementationError();
	}

	public boolean isLocalClass() {
		throw new IncompleteImplementationError();
	}

	public boolean isMemberClass() {
		throw new IncompleteImplementationError();
	}

	public Class<?>[] getClasses() {
		throw new IncompleteImplementationError();
	}

	public Field[] getFields() {
		Class<?> target = this;
		ArrayList<Field> fields = new ArrayList<Field>();
		while (target != null) {
			Field[] decl = target.getDeclaredFields();
			for (Field f : decl) {
				if (f.isPublic()) {
					fields.add(f);
				}
			}
			target = target.getSuperclass();
		}
		return fields.toArray(new Field[fields.size()]);
	}

	public Method[] getMethods() {
		Class<?> target = this;
		ArrayList<Method> methods = new ArrayList<Method>();
		while (target != null) {
			Method[] decl = target.getDeclaredMethods();
			for (Method m : decl) {
				if (m.isPublic()) {
					methods.add(m);
				}
			}
			target = target.getSuperclass();
		}
		return methods.toArray(new Field[methods.size()]);
	}

	public Constructor<?>[] getConstructors() {
		Constructor<?>[] cstr = getDeclaredConstructors();
		int total = 0;
		for (int i = 0; i < cstr.length; i++) {
			if (cstr[i].isPublic()) {
				total++;
			}
		}
		Constructor<?>[] out = new Constructor<?>[total];
		int j = 0;
		for (int i = 0; i < cstr.length; i++) {
			if (cstr[i].isPublic()) {
				out[j++] = cstr[i];
			}
		}
		return out;
	}

	public Field getField(String name) throws NoSuchFieldException {
		if (name == null) {
			throw new NullPointerException();
		}
		VMClass target = this.cls;
		while (target != null) {
			Field f = target.getField(name);
			if (f != null && f.isPublic()) {
				return f;
			}
			target = target.getSuperClass();
		}
		throw new NoSuchFieldException("no such field " + name + " on " + this.getName());
	}

	public Method getMethod(String name, Class<?>... parameterTypes) throws NoSuchMethodException {
		if (name == null) {
			throw new NullPointerException();
		}
		if (name.equals("<init>") || name.equals("<clinit>")) {
			throw new NoSuchMethodException("method " + name + " is not a valid target");
		}
		VMClass[] params = new VMClass[parameterTypes.length];
		for (int i = 0; i < params.length; i++) {
			params[i] = parameterTypes[i].cls;
		}
		VMClass target = this.cls;
		while (target != null) {
			Method m = target.getMethod(name, params);
			if (m != null && m.isPublic()) {
				return m;
			}
			target = target.getSuperClass();
		}
		throw new NoSuchMethodException("no such method " + name + " on " + this.getName() + " with given parameters");
	}

	public Constructor<T> getConstructor(Class<?>... parameterTypes) throws NoSuchMethodException {
		VMClass[] params = new VMClass[parameterTypes.length];
		for (int i = 0; i < params.length; i++) {
			params[i] = parameterTypes[i].cls;
		}
		Constructor<?> cstr = cls.getConstructor(params);
		if (cstr == null) {
			throw new NoSuchMethodException("no constructor on " + this.getName() + " with given parameters.");
		}
		return (Constructor<T>) cstr;
	}

	public Class<?>[] getDeclaredClasses() {
		throw new IncompleteImplementationError();
	}

	public Field[] getDeclaredFields() {
		return cls.getFields();
	}

	public Method[] getDeclaredMethods() {
		return cls.getMethods();
	}

	public Constructor<?>[] getDeclaredConstructors() {
		return cls.getConstructors();
	}

	public Field getDeclaredField(String name) throws NoSuchFieldException {
		if (name == null) {
			throw new NullPointerException();
		}
		Field f = cls.getField(name);
		if (f == null) {
			throw new NoSuchFieldException("no such field " + name + " on " + this.getName());
		}
		return f;
	}

	public Method getDeclaredMethod(String name, Class<?>... parameterTypes) throws NoSuchMethodException {
		if (name == null) {
			throw new NullPointerException();
		}
		if (name.equals("<init>") || name.equals("<clinit>")) {
			throw new NoSuchMethodException("method " + name + " is not a valid target");
		}
		VMClass[] params = new VMClass[parameterTypes.length];
		for (int i = 0; i < params.length; i++) {
			params[i] = parameterTypes[i].cls;
		}
		Method m = cls.getMethod(name, params);
		if (m == null) {
			throw new NoSuchMethodException("no such method " + name + " on " + this.getName() + " with given parameters");
		}
		return m;
	}

	public Constructor<T> getDeclaredConstructor(Class<?>... parameterTypes) throws NoSuchMethodException {
		VMClass[] params = new VMClass[parameterTypes.length];
		for (int i = 0; i < params.length; i++) {
			params[i] = parameterTypes[i].cls;
		}
		Constructor<?> c = cls.getConstructor(params);
		if (c == null) {
			throw new NoSuchMethodException("no constructor on " + this.getName() + " with given parameters");
		}
		return (Constructor<T>) c;
	}
	
	private String convertResource(String name) {
		if (name.startsWith("/")) {
			return name.substring(1);
		} else {
			String cn = this.getName();
			int i = cn.lastIndexOf('.');
			if (i == -1) {
				return name;
			} else {
				return cn.substring(0, i).replace('.', '/') + "/" + name;
			}
		}
	}

	public InputStream getResourceAsStream(String name) {
		return this.getClassLoader().getResourceAsStream(convertResource(name));
	}

	public URL getResource(String name) {
		return this.getClassLoader().getResource(convertResource(name));
	}

	// public ProtectionDomain getProtectionDomain() {}

	public boolean desiredAssertionStatus() {
		return this.getClassLoader().getDesiredAssertionStatus(this.getName());
	}

	public boolean isEnum() {
		return cls.isEnum();
	}

	public T[] getEnumConstants() {
		if (isEnum()) {
			return (T[]) this.getMethod("values").invoke(null);
		} else {
			return null;
		}
	}

	public T cast(Object obj) {
		if (obj == null || this.isInstance(obj)) {
			return (T) obj;
		} else {
			throw new ClassCastException();
		}
	}

	public <U> Class<? extends U> asSubclass(Class<U> clazz) {
		if (clazz.isAssignableFrom(this)) {
			return (Class<? extends U>) this;
		} else {
			throw new ClassCastException();
		}
	}
}
