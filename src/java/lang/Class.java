package java.lang;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;

import org.apache.bcel.classfile.Attribute;

import com.celskeggs.bell.support.IncompleteImplementationError;
import com.celskeggs.bell.vm.VMClass;

public final class Class<T> /*
							 * implements Serializable, GenericDeclaration,
							 * Type, AnnotatedElement
							 */ {

	private final VMClass cls;

	// Referenced by com.celskeggs.bell.vm.VMNatives.callClassConstructor(VMClass)
	private Class(VMClass cls) {
		if (cls == null) {
			throw new NullPointerException();
		}
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

	public static Class<?> forName(String className, boolean initialize, ClassLoader loader)
			throws ClassNotFoundException {
		if (className.indexOf('/') != -1) {
			throw new ClassNotFoundException();
		}
		if (className.endsWith("[]")) {
			return forName(className.substring(0, className.length() - 2), false, loader).cls.getArrayOf()
					.getRealClass();
		}
		Class<?> cls;
		if (loader == null) {
			cls = ClassLoader.findBootstrapClass(className);
		} else {
			cls = loader.loadClass(className);
		}
		if (initialize) {
			cls.cls.ensureInitialized();
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
		return Modifier.isInterface(cls.getModifiers());
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

	private static final int ACC_SYNTHETIC = 0x1000;

	public boolean isSynthetic() {
		return (cls.getModifiers() & ACC_SYNTHETIC) != 0;
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
		VMClass ct = cls.getComponentType();
		return ct == null ? null : ct.getRealClass();
	}

	private static final int MODIFIERS_ALLOWED = (Modifier.classModifiers() & ~Modifier.STRICT) | Modifier.INTERFACE;

	public int getModifiers() {
		return cls.getModifiers() & MODIFIERS_ALLOWED;
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
				if (Modifier.isPublic(f.getModifiers())) {
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
				if (Modifier.isPublic(m.getModifiers())) {
					methods.add(m);
				}
			}
			target = target.getSuperclass();
		}
		return methods.toArray(new Method[methods.size()]);
	}

	public Constructor<?>[] getConstructors() {
		Constructor<?>[] cstr = getDeclaredConstructors();
		int total = 0;
		for (int i = 0; i < cstr.length; i++) {
			if (Modifier.isPublic(cstr[i].getModifiers())) {
				total++;
			}
		}
		Constructor<?>[] out = new Constructor<?>[total];
		int j = 0;
		for (int i = 0; i < cstr.length; i++) {
			if (Modifier.isPublic(cstr[i].getModifiers())) {
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
			if (f != null && Modifier.isPublic(f.getModifiers())) {
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
			if (m != null && Modifier.isPublic(m.getModifiers())) {
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
			throw new NoSuchMethodException(
					"no such method " + name + " on " + this.getName() + " with given parameters");
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

	public boolean desiredAssertionStatus() {
		return this.getClassLoader().getDesiredAssertionStatus(this.getName());
	}

	private static final int ACC_ENUM = 0x4000;

	public boolean isEnum() {
		return (cls.getModifiers() & ACC_ENUM) != 0;
	}

	public T[] getEnumConstants() {
		if (isEnum()) {
			try {
				return (T[]) this.getMethod("values").invoke(null);
			} catch (Exception e) {
				return null;
			}
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

    public Package getPackage() {
        throw new IncompleteImplementationError();
    }
}
