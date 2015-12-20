package java.util;

public interface Map<K, V> {
	public static interface Entry<K, V> {
		boolean equals(Object o);

		K getKey();

		V getValue();

		int hashCode();

		V setValue(V value);
	}

	void clear();

	boolean containsKey(Object key);

	boolean containsValue(Object value);

	Set<Map.Entry<K, V>> entrySet();

	boolean equals(Object o);

	V get(Object key);

	int hashCode();

	boolean isEmpty();

	Set<K> keySet();

	V put(K key, V value);

	void putAll(Map<? extends K, ? extends V> m);

	V remove(Object key);

	int size();

	Collection<V> values();
}
