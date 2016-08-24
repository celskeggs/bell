package com.celskeggs.bell.vm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.celskeggs.bell.support.IncompleteImplementationError;
import com.celskeggs.bell.vm.data.DatClass;
import com.celskeggs.bell.vm.data.DatMethod;
import com.celskeggs.bell.vm.data.DatType;

public abstract class VMClass {
	// TODO: do this
	public static final VMClass BOOLEAN = null;
	public static final VMClass BYTE = null;
	public static final VMClass CHAR = null;
	public static final VMClass INT = null;
	public static final VMClass LONG = null;
	public static final VMClass DOUBLE = null;
	public static final VMClass FLOAT = null;
	public static final VMClass SHORT = null;
	public static final VMClass VOID = null;

	private Class<?> realClass;

	VMClass() {
	}

	public Class<?> getRealClass() {
		if (realClass == null) {
			realClass = (Class<?>) VMNatives.intToObject(
					VMAccess.invoke1(VMNatives.getRootSlab().classConstructor, VMNatives.objectToInt(this)));
		}
		return realClass;
	}

	public abstract boolean isPrimitive();

	public abstract Object newInstance() throws InstantiationException;

	public abstract String getName();

	public abstract boolean isArray();

	public abstract boolean isAssignableFrom(VMClass cls);

	public abstract int getInterfaceCount();

	public abstract VMClass getInterfaceN(int i);

	public abstract VMClass getSuperClass();

	static final class Array extends VMClass {
		private final VMClass element;
		private VMClass asArray;

		public Array(VMClass element) {
			if (element == null) {
				throw new NullPointerException();
			}
			this.element = element;
		}

		@Override
		public VMClass getArrayOf() {
			if (asArray == null) {
				asArray = new Array(this);
			}
			return asArray;
		}

		@Override
		public boolean isPrimitive() {
			return false;
		}

		@Override
		public Object newInstance() throws InstantiationException {
			throw new InstantiationException("Class is an array!");
		}

		@Override
		public String getName() {
			return getDescriptor();
		}

		@Override
		public String getDescriptor() {
			return "[" + element.getDescriptor();
		}

		@Override
		public boolean isArray() {
			return true;
		}

		@Override
		public boolean isAssignableFrom(VMClass cls) {
			if (cls == this) {
				return true;
			} else if (!cls.isArray()) {
				return false;
			} else {
				return this.getComponentType().isAssignableFrom(cls.getComponentType());
			}
		}

		@Override
		public int getInterfaceCount() {
			// TODO: include Serializable
			return 1;
		}

		@Override
		public VMClass getInterfaceN(int i) {
			if (i != 0) {
				throw new VirtualMachineError();
			}
			return VMAccess.getVMClassByName("java/lang/Cloneable");
		}

		@Override
		public VMClass getSuperClass() {
			return VMAccess.getVMClassByName("java/lang/Object");
		}

		@Override
		public VMClass getComponentType() {
			return element;
		}

		@Override
		public Field getField(String name) {
			throw new IncompleteImplementationError();
		}

		@Override
		public Method getMethod(String name, VMClass[] params) {
			throw new IncompleteImplementationError();
		}

		@Override
		public Constructor<?> getConstructor(VMClass[] params) {
			throw new IncompleteImplementationError();
		}

		@Override
		public Field[] getFields() {
			throw new IncompleteImplementationError();
		}

		@Override
		public Method[] getMethods() {
			throw new IncompleteImplementationError();
		}

		@Override
		public Constructor<?>[] getConstructors() {
			throw new IncompleteImplementationError();
		}

		@Override
		public void ensureInitialized() {
			this.element.ensureInitialized();
		}

		@Override
		public int getModifiers() {
			return (element.getModifiers() & (Modifier.PUBLIC | Modifier.PRIVATE | Modifier.PROTECTED))
					| Modifier.FINAL;
		}

		@Override
		public ClassLoader getClassLoader() {
			return element.getClassLoader();
		}
	}

	static final class Java extends VMClass {
		private final DatClass cls;
		private VMClass asArray;
		private boolean initialized = false;
		private DatMethod nullaryConstructor = null;

		Java(DatClass cls) {
			this.cls = cls;
		}

		@Override
		public VMClass getArrayOf() {
			if (asArray == null) {
				asArray = new Array(this);
			}
			return asArray;
		}

		@Override
		public boolean isPrimitive() {
			return false;
		}

		private Object newRawInstance() {
			Object chunk = VMNatives.allocateZeroedChunk(cls.instance_size);
			VMNatives.writeObject(chunk, VMFormat.CHUNK_TYPE_OFFSET, this.cls);
			return chunk;
		}

		private DatMethod getNullaryConstructor() {
			// TODO: optimize so that this doesn't recheck every time for
			// classes with no nullary constructor
			if (this.nullaryConstructor == null) {
				for (DatMethod method : cls.methods) {
					if (method.parameter_types.length == 0 && method.return_type.isVoid()
							&& "<init>".equals(VMAccess.getStringByDat(method.method_name))) {
						this.nullaryConstructor = method;
						break;
					}
				}
			}
			return this.nullaryConstructor;
		}

		public Object newInstance() throws InstantiationException {
			this.ensureInitialized();
			Object rawInstance = newRawInstance();
			DatMethod ncu = getNullaryConstructor();
			if (ncu == null) {
				throw new InstantiationException("No nullary constructor!");
			}
			// TODO: check access
			try {
				VMAccess.invoke1(ncu, VMNatives.objectToInt(rawInstance));
			} catch (Throwable thr) {
				throw new InstantiationException("Exception while instantiating class: " + thr);
			}
			return rawInstance;
		}

		@Override
		public String getName() {
			return VMAccess.getStringByDat(cls.name);
		}

		@Override
		public boolean isArray() {
			return false;
		}

		@Override
		public boolean isAssignableFrom(VMClass cls) {
			if (cls == this) {
				return true;
			} else if (cls.isPrimitive()) {
				return false;
			} else if (cls.isArray() || Modifier.isInterface(cls.getModifiers())) {
				// TODO: check if this is correct behavior for interfaces
				return "java/lang/Object".equals(getName());
			} else {
				VMClass superClass = cls.getSuperClass();
				if (superClass != null && this.isAssignableFrom(superClass)) {
					return true;
				} else {
					int ic = cls.getInterfaceCount();
					for (int i = 0; i < ic; i++) {
						VMClass vmc = cls.getInterfaceN(i);
						if (this.isAssignableFrom(vmc)) {
							return true;
						}
					}
					return false;
				}
			}
		}

		@Override
		public int getInterfaceCount() {
			return cls.interfaces.length;
		}

		@Override
		public VMClass getInterfaceN(int i) {
			return VMAccess.getVMClassByDatClass(cls.interfaces[i]);
		}

		@Override
		public VMClass getSuperClass() {
			return VMAccess.getVMClassByDatClass(cls.super_class);
		}

		@Override
		public VMClass getComponentType() {
			return null;
		}

		@Override
		public Field getField(String name) {
			throw new IncompleteImplementationError();
		}

		@Override
		public Method getMethod(String name, VMClass[] params) {
			throw new IncompleteImplementationError();
		}

		@Override
		public Constructor<?> getConstructor(VMClass[] params) {
			throw new IncompleteImplementationError();
		}

		@Override
		public Field[] getFields() {
			throw new IncompleteImplementationError();
		}

		@Override
		public Method[] getMethods() {
			throw new IncompleteImplementationError();
		}

		@Override
		public Constructor<?>[] getConstructors() {
			throw new IncompleteImplementationError();
		}

		@Override
		public String getDescriptor() {
			return "L" + getName() + ";";
		}

		@Override
		public void ensureInitialized() {
			if (!initialized) {
				initialized = true;
				// TODO: initialize
				throw new IncompleteImplementationError();
			}
		}

		@Override
		public int getModifiers() {
			return cls.flags;
		}

		@Override
		public ClassLoader getClassLoader() {
			return null; // bootstrap class
		}
	}

	public abstract String getDescriptor();

	public abstract VMClass getComponentType();

	// null if not found; check only THIS class; don't check for public
	public abstract Field getField(String name);

	// null if not found; check only THIS class; don't check for public
	public abstract Method getMethod(String name, VMClass[] params);

	// null if not found; check only THIS class; don't check for public
	public abstract Constructor<?> getConstructor(VMClass[] params);

	// check only THIS class; don't check for public
	public abstract Field[] getFields();

	// check only THIS class; don't check for public
	public abstract Method[] getMethods();

	public abstract Constructor<?>[] getConstructors();

	public abstract VMClass getArrayOf();

	public abstract void ensureInitialized();

	public abstract int getModifiers();

	public abstract ClassLoader getClassLoader();

	public static VMClass vmClassOf(Object object) {
		// TODO: avoid checked casting here
		Object type = VMNatives.readObject(object, VMFormat.CHUNK_TYPE_OFFSET);
		// could either be DatClass or DatType (for an array)
		if (VMNatives.readObject(type, VMFormat.CHUNK_TYPE_OFFSET) == VMNatives.getRootSlab().datClassDatClass) {
			return VMAccess.getVMClassByDatClass((DatClass) type);
		} else {
			return VMAccess.getVMClassByDatType((DatType) type);
		}
	}

	public static Class<?> classOf(Object object) {
		return vmClassOf(object).getRealClass();
	}
}
