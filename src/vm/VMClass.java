package vm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class VMClass {
	// TODO: find this somehow
	private static final int ALLOCATE_CLASS_FOR_VMCLASS_METHOD_ID = -1;

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
			realClass = allocateClassForVMClass(this);
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
		private final int id;
		private VMClass asArray;
		private boolean initialized = false;

		Java(int id) {
			this.id = id;
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

		public Object newInstance() throws InstantiationException {
			this.ensureInitialized();
			Object rawInstance = VMDispatch.rawNewObject(id);
			int ncu = VMAccess.getNullConstructor(id);
			if (ncu == 0) {
				throw new InstantiationException("No nullary constructor!");
			}
			// TODO: check access
			try {
				VMNatives.call1(ncu, VMNatives.objectToID(rawInstance));
			} catch (Throwable thr) {
				throw new InstantiationException("Exception while instantiating class: " + thr);
			}
			return rawInstance;
		}

		@Override
		public String getName() {
			return VMAccess.getVMClassName(id);
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
			} else if (this.isAssignableFrom(cls.getSuperClass())) {
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

		@Override
		public int getInterfaceCount() {
			return VMAccess.getInterfaceCount(id);
		}

		@Override
		public VMClass getInterfaceN(int i) {
			return VMAccess.getVMClassByID(VMAccess.getInterfaceN(id, i));
		}

		@Override
		public VMClass getSuperClass() {
			return VMAccess.getSuperClass(id);
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
			return VMAccess.getVMClassFlags(id);
		}

		@Override
		public ClassLoader getClassLoader() {
			return null; // bootstrap class
		}
	}

	private static Class<?> allocateClassForVMClass(VMClass id) {
		return (Class<?>) VMNatives
				.idToObject(VMNatives.call1(ALLOCATE_CLASS_FOR_VMCLASS_METHOD_ID, VMNatives.objectToID(id)));
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
}
