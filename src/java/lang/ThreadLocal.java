package java.lang;

import java.util.HashMap;

public class ThreadLocal<T> {
	// TODO: replace stub implementation
	
	private static Object getThread() {
		return null;
	}
	
	private final HashMap<Object, T> values = new HashMap<Object, T>();
	
	public ThreadLocal() {
	}
	
	protected T initialValue() {
		return null;
	}
	
	public T get() {
		Object thread = getThread();
		synchronized (values) {
			if (values.containsKey(thread)) {
				return values.get(thread);
			}
		}
		T value = initialValue();
		synchronized (values) {
			values.put(thread, value);
		}
		return value;
	}
	
	public void set(T value) {
		Object thread = getThread();
		synchronized (values) {
			values.put(thread, value);
		}
	}
	
	public void remove() {
		Object thread = getThread();
		synchronized (values) {
			values.remove(thread);
		}
	}
}
