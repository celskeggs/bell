package java.util;

import com.colbyskeggs.support.EnumerationAdapter;

public class Hashtable<K, V> extends Dictionary<K, V>
		implements Map<K, V> /* , Cloneable, Serializable */ {

	private final HashMap<K, V> internal;

	public Hashtable() {
		this(11, 0.75f);
	}

	public Hashtable(int initialCapacity) {
		this(initialCapacity, 0.75f);
	}

	public Hashtable(int initialCapacity, float loadFactor) {
		internal = new HashMap<K, V>(initialCapacity, loadFactor);
	}

	public Hashtable(Map<? extends K, ? extends V> m) {
		this(m.size() * 4 / 3, 0.75f); // TODO: check this factor
		putAll(m);
	}

	public synchronized int size() {
		return internal.size();
	}

	public synchronized boolean isEmpty() {
		return internal.isEmpty();
	}

	public synchronized Enumeration<K> keys() {
		// TODO: synchronized returned stuff?
		return new EnumerationAdapter<K>(internal.keySet().iterator());
	}

	public synchronized Enumeration<V> elements() {
		return new EnumerationAdapter<V>(internal.values().iterator());
	}

	public synchronized boolean contains(Object value) {
		if (value == null) {
			throw new NullPointerException();
		}
		return internal.containsValue(value);
	}

	public synchronized boolean containsValue(Object value) {
		if (value == null) {
			throw new NullPointerException();
		}
		return internal.containsValue(value);
	}

	public synchronized boolean containsKey(Object key) {
		if (key == null) {
			throw new NullPointerException();
		}
		return internal.containsKey(key);
	}

	public synchronized V get(Object key) {
		if (key == null) {
			throw new NullPointerException();
		}
		return internal.get(key);
	}

	protected synchronized void rehash() {
		internal.increaseCapacity(internal.capacity() * 2);
	}

	public synchronized V put(K key, V value) {
		if (key == null || value == null) {
			throw new NullPointerException();
		}
		return internal.put(key, value);
	}

	public synchronized V remove(Object key) {
		if (key == null) {
			throw new NullPointerException();
		}
		return internal.remove(key);
	}

	public synchronized void putAll(Map<? extends K, ? extends V> t) {
		for (Map.Entry<? extends K, ? extends V> ent : t.entrySet()) {
			put(ent.getKey(), ent.getValue());
		}
	}

	public synchronized void clear() {
		internal.clear();
	}

	// public Object clone(); TODO

	public synchronized String toString() {
		return internal.toString();
	}

	public synchronized Set<K> keySet() {
		return internal.keySet();
	}

	public synchronized Set<Map.Entry<K, V>> entrySet() {
		return internal.entrySet();
	}

	public synchronized Collection<V> values() {
		return internal.values();
	}

	public synchronized boolean equals(Object o) {
		return internal.equals(o);
	}

	public synchronized int hashCode() {
		return internal.hashCode();
	}
}
