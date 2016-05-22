package java.lang;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import com.celskeggs.support.CUtil;

import vm.VMAccess;

public abstract class ClassLoader {

	// null for bootstrap
	private final ClassLoader parent;
	private static final HashMap<Class<? extends ClassLoader>, Boolean> registeredAsParallel = new HashMap<Class<? extends ClassLoader>, Boolean>();
	private final HashMap<String, Class<?>> loadedByUs = new HashMap<String, Class<?>>();
	private boolean defaultAssertionStatus = false;
	private final boolean parallel;
	private final HashMap<String, Boolean> packageAssertionStatuses = new HashMap<String, Boolean>();
	private final HashMap<String, Boolean> classAssertionStatuses = new HashMap<String, Boolean>();

	protected static boolean registerAsParallelCapable() {
		// TODO: needs to be able to check the caller
		if (true) {
			throw new IncompleteImplementationError();
		}
		Class<? extends ClassLoader> who = null;
		if (!registeredAsParallel.containsKey(who)) {
			Class<?> c = who.getSuperclass();
			if (c == ClassLoader.class || registeredAsParallel.get(c) == Boolean.TRUE) {
				registeredAsParallel.put(who, Boolean.TRUE);
			}
		}
		return registeredAsParallel.get(who);
	}

	public ClassLoader() {
		this(getSystemClassLoader());
	}

	public ClassLoader(ClassLoader parent) {
		this.parent = parent;
		Class<? extends ClassLoader> c = this.getClass();
		if (!registeredAsParallel.containsKey(c)) {
			registeredAsParallel.put(c, false);
		}
		parallel = registeredAsParallel.get(c);
	}

	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return loadClass(name, false);
	}

	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		synchronized (getClassLoadingLock(name)) {
			Class<?> class_ = findLoadedClass(name);
			if (class_ == null) {
				try {
					return parent == null ? findBootstrapClass(name) : parent.loadClass(name, resolve);
				} catch (ClassNotFoundException ex) {
					class_ = findClass(name);
				}
			}
			if (resolve) {
				resolveClass(class_);
			}
			return class_;
		}
	}

	static Class<?> findBootstrapClass(String name) throws java.lang.ClassNotFoundException {
		if (name.indexOf('/') != 0) {
			throw new ClassNotFoundException(name); // TODO: correct behavior?
		}
		return VMAccess.getClassByName(name.replace('.', '/'));
	}

	private final HashMap<String, Object> locks = new HashMap<String, Object>();

	protected Object getClassLoadingLock(String className) {
		if (parallel) {
			if (className == null) {
				throw new NullPointerException();
			}
			Object lock = locks.get(className);
			if (lock == null) {
				synchronized (locks) {
					if (locks.get(className) == null) {
						lock = new Object();
						locks.put(className, lock);
					}
				}
			}
			return lock;
		} else {
			return this;
		}
	}

	protected Class<?> findClass(String name) throws ClassNotFoundException {
		throw new ClassNotFoundException(name);
	}

	@Deprecated
	protected final Class<?> defineClass(byte[] b, int off, int len) throws ClassFormatError {
		throw new IncompleteImplementationError();
	}

	protected final Class<?> defineClass(String name, byte[] b, int off, int len) throws ClassFormatError {
		// TODO: translate dynamically or something?
		loadedByUs.put(name, null); // incomplete
		throw new IncompleteImplementationError();
	}

	protected final void resolveClass(Class<?> c) {
		throw new IncompleteImplementationError();
	}

	protected final Class<?> findSystemClass(String name) throws ClassNotFoundException {
		return getSystemClassLoader().loadClass(name);
	}

	protected final Class<?> findLoadedClass(String name) {
		return loadedByUs.get(name);
	}

	protected final void setSigners(Class<?> c, Object[] signers) {
		throw new IncompleteImplementationError();
	}

	public URL getResource(String name) {
		URL found;
		if (parent == null) {
			found = null; // TODO: actually try
		} else {
			found = parent.getResource(name);
		}
		if (found != null) {
			return found;
		} else {
			return findResource(name);
		}
	}

	public Enumeration<URL> getResources(String name) throws IOException {
		Enumeration<URL> parentRes;
		if (parent == null) {
			parentRes = CUtil.emptyEnumeration();
			// TODO: actually try to include
		} else {
			parentRes = parent.getResources(name);
		}
		return CUtil.concatEnumerations(parentRes, findResources(name));
	}

	protected URL findResource(String name) {
		return null;
	}

	protected Enumeration<URL> findResources(String name) throws IOException {
		return CUtil.emptyEnumeration();
	}

	public static URL getSystemResource(String name) {
		return getSystemClassLoader().getResource(name);
	}

	public static Enumeration<URL> getSystemResources(String name) {
		try {
			return getSystemClassLoader().getResources(name);
		} catch (IOException e) {
			return CUtil.emptyEnumeration(); // TODO: correct behavior?
		}
	}

	public InputStream getResourceAsStream(String name) {
		URL resource = getResource(name);
		if (resource == null) {
			return null;
		}
		try {
			return resource.openStream();
		} catch (IOException e) {
			return null;
		}
	}

	public static InputStream getSystemResourceAsStream(String name) {
		return getSystemClassLoader().getResourceAsStream(name);
	}

	public final ClassLoader getParent() {
		return parent;
	}

	private static ClassLoader systemLoader = null;

	// TODO: invoke this early in the startup sequence
	public static ClassLoader getSystemClassLoader() {
		if (systemLoader == null) {
			synchronized (ClassLoader.class) {
				if (systemLoader == null) {
					systemLoader = createSystemClassLoader();
					// TODO: set as context class loader of invoking thread
				}
			}
		}
		return systemLoader;
	}

	private static boolean creatingSystemClassLoader;

	private static ClassLoader createSystemClassLoader() {
		if (creatingSystemClassLoader) {
			throw new IllegalStateException();
		}
		creatingSystemClassLoader = true;
		ClassLoader cl = null; // TODO: do this correctly
		if (true) {
			throw new IncompleteImplementationError();
		}
		String loader = System.getProperty("java.system.class.loader");
		if (loader != null && !loader.isEmpty()) {
			try {
				cl = cl.loadClass(loader).asSubclass(ClassLoader.class).getConstructor(ClassLoader.class)
						.newInstance(cl);
			} catch (Throwable thr) {
				throw new Error("Cannot construct system class loader", thr);
			}
		}
		return cl;
	}

	protected String findLibrary(String libname) {
		return null;
	}

	public void setDefaultAssertionStatus(boolean enabled) {
		defaultAssertionStatus = enabled;
	}

	public void setPackageAssertionStatus(String packageName, boolean enabled) {
		packageAssertionStatuses.put(packageName, enabled);
	}

	public void setClassAssertionStatus(String className, boolean enabled) {
		classAssertionStatuses.put(className, enabled);
	}

	boolean getDesiredAssertionStatus(String className) {
		Boolean b = classAssertionStatuses.get(className);
		if (b != null) {
			return b;
		}
		String packageName = className;
		while (packageName.lastIndexOf('.') != -1) {
			packageName = packageName.substring(0, packageName.lastIndexOf('.'));
			b = packageAssertionStatuses.get(packageName);
			if (b != null) {
				return b;
			}
		}
		return defaultAssertionStatus;
	}

	public void clearAssertionStatus() {
		defaultAssertionStatus = false;
		packageAssertionStatuses.clear();
		classAssertionStatuses.clear();
	}
}
