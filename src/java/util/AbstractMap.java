package java.util;

public abstract class AbstractMap<K, V> implements Map<K, V> {

	private final class DependentValueCollection extends AbstractCollection<V> {

		private final Set<java.util.Map.Entry<K, V>> entrySet;

		public DependentValueCollection(Set<java.util.Map.Entry<K, V>> entrySet) {
			this.entrySet = entrySet;
		}

		public Iterator<V> iterator() {
			final Iterator<java.util.Map.Entry<K, V>> iterator = entrySet.iterator();
			return new Iterator<V>() {
				public boolean hasNext() {
					return iterator.hasNext();
				}

				public V next() {
					return iterator.next().getValue();
				}

				public void remove() {
					iterator.remove();
				}
			};
		}

		public int size() {
			return AbstractMap.this.size();
		}

		public boolean contains(Object o) {
			return containsValue(o);
		}

		public void clear() {
			AbstractMap.this.clear();
		}
	}

	private final class DependentKeySet extends AbstractSet<K> {

		private final Set<java.util.Map.Entry<K, V>> entrySet;

		public DependentKeySet(Set<Map.Entry<K, V>> entrySet) {
			this.entrySet = entrySet;
		}

		public Iterator<K> iterator() {
			final Iterator<java.util.Map.Entry<K, V>> iterator = entrySet.iterator();
			return new Iterator<K>() {
				public boolean hasNext() {
					return iterator.hasNext();
				}

				public K next() {
					return iterator.next().getKey();
				}

				public void remove() {
					iterator.remove();
				}
			};
		}

		public int size() {
			return AbstractMap.this.size();
		}

		public boolean contains(Object o) {
			return containsKey(o);
		}

		public void clear() {
			AbstractMap.this.clear();
		}
	}

	public static class SimpleEntry<K, V> implements Map.Entry<K, V> {
		private K key;
		private V value;

		public SimpleEntry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		public SimpleEntry(Map.Entry<? extends K, ? extends V> entry) {
			this.key = entry.getKey();
			this.value = entry.getValue();
		}

		public K getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}

		public V setValue(V value) {
			V last = this.value;
			this.value = value;
			return last;
		}

		public int hashCode() {
			return Objects.hashCode(key) ^ Objects.hashCode(value);
		}

		public boolean equals(Object o) {
			return o instanceof Map.Entry && Objects.equals(key, ((Map.Entry<?, ?>) o).getKey())
					&& Objects.equals(value, ((Map.Entry<?, ?>) o).getValue());
		}

		public String toString() {
			return key + "=" + value;
		}
	}

	public static class SimpleImmutableEntry<K, V> implements Map.Entry<K, V> {
		private final K key;
		private final V value;

		public SimpleImmutableEntry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		public SimpleImmutableEntry(Map.Entry<? extends K, ? extends V> entry) {
			this.key = entry.getKey();
			this.value = entry.getValue();
		}

		public K getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}

		public V setValue(V value) {
			throw new UnsupportedOperationException("Entry is immutable.");
		}

		public int hashCode() {
			return Objects.hashCode(key) ^ Objects.hashCode(value);
		}

		public boolean equals(Object o) {
			return o instanceof Map.Entry && Objects.equals(key, ((Map.Entry<?, ?>) o).getKey())
					&& Objects.equals(value, ((Map.Entry<?, ?>) o).getValue());
		}

		public String toString() {
			return key + "=" + value;
		}
	}

	private Set<K> keySet;
	private Collection<V> valueCollection;

	protected AbstractMap() {
	}

	public int size() {
		return entrySet().size();
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public boolean containsValue(Object value) {
		for (Map.Entry<K, V> ent : entrySet()) {
			if (Objects.equals(ent.getValue(), value)) {
				return true;
			}
		}
		return false;
	}

	public boolean containsKey(Object key) {
		for (Map.Entry<K, V> ent : entrySet()) {
			if (Objects.equals(ent.getKey(), key)) {
				return true;
			}
		}
		return false;
	}

	public V get(Object key) {
		for (Map.Entry<K, V> ent : entrySet()) {
			if (Objects.equals(ent.getKey(), key)) {
				return ent.getValue();
			}
		}
		return null;
	}

	public V put(K key, V value) {
		throw new UnsupportedOperationException("Map is immutable!");
	}

	public V remove(Object key) {
		for (Iterator<java.util.Map.Entry<K, V>> iterator = entrySet().iterator(); iterator.hasNext();) {
			Map.Entry<K, V> ent = iterator.next();
			if (Objects.equals(ent.getKey(), key)) {
				V out = ent.getValue();
				iterator.remove();
				return out;
			}
		}
		return null;
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		for (Map.Entry<? extends K, ? extends V> ent : m.entrySet()) {
			this.put(ent.getKey(), ent.getValue());
		}
	}

	public void clear() {
		entrySet().clear();
	}

	public Set<K> keySet() {
		if (keySet == null) {
			keySet = new DependentKeySet(entrySet());
		}
		return keySet;
	}

	public Collection<V> values() {
		if (valueCollection == null) {
			valueCollection = new DependentValueCollection(entrySet());
		}
		return valueCollection;
	}

	public abstract Set<Map.Entry<K, V>> entrySet();

	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (!(o instanceof Map)) {
			return false;
		} else {
			Map<?, ?> m = (Map<?, ?>) o;
			if (m.size() != size()) {
				return false;
			}
			Set<? extends Map.Entry<?, ?>> other = m.entrySet();
			for (Map.Entry<K, V> ent : entrySet()) {
				if (!other.contains(ent)) {
					return false;
				}
			}
			return true;
		}
	}

	public int hashCode() {
		int total = 0;
		for (Map.Entry<K, V> ent : entrySet()) {
			total += ent.hashCode();
		}
		return total;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("{");
		for (Map.Entry<K, V> ent : entrySet()) {
			sb.append(ent.toString()).append(", ");
		}
		if (sb.length() > 2) {
			sb.setLength(sb.length() - 2);
		}
		sb.append("}");
		return sb.toString();
	}

	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
