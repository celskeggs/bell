package java.util;

public class HashMap<K, V> extends AbstractMap<K, V> {

	private static final class LinkedEntry<K, V> extends SimpleEntry<K, V> {
		public LinkedEntry<K, V> next;

		public LinkedEntry(K key, V value, LinkedEntry<K, V> next) {
			super(key, value);
			this.next = next;
		}
	}

	private final class SetView extends AbstractSet<Map.Entry<K, V>> {
		public Iterator<Map.Entry<K, V>> iterator() {
			return new EntryIterator();
		}

		public int size() {
			return count;
		}
	}

	private final class EntryIterator implements Iterator<Map.Entry<K, V>> {
		private int index = 0;
		private boolean canRemove;
		private K keyRemove;
		private LinkedEntry<K, V> cur = null;

		public boolean hasNext() {
			while (cur == null) {
				if (index >= entries.length) {
					return false;
				}
				cur = entries[index++];
			}
			return true;
		}

		public Map.Entry<K, V> next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			Map.Entry<K, V> out = cur;
			canRemove = true;
			keyRemove = cur.getKey();
			cur = cur.next;
			return out;
		}

		public void remove() {
			if (!canRemove) {
				throw new IllegalStateException();
			}
			canRemove = false;
			int target = bucketFor(keyRemove);
			LinkedEntry<K, V> ent = entries[target];
			LinkedEntry<K, V> last = null;
			while (!Objects.equals(ent.getKey(), keyRemove)) {
				last = ent;
				ent = last.next;
				if (ent == null) {
					throw new ConcurrentModificationException();
				}
			}
			if (last == null) {
				entries[target] = ent.next;
			} else {
				last.next = ent.next;
			}
			keyRemove = null;
		}
	}

	private LinkedEntry<K, V>[] entries;
	private final float loadFactor;
	private SetView setView;
	private int count = 0;

	public HashMap() {
		this(16, 0.75f);
	}

	public HashMap(int initialCapacity) {
		this(initialCapacity, 0.75f);
	}

	public HashMap(int initialCapacity, float loadFactor) {
		if (initialCapacity < 0 || loadFactor <= 0) {
			throw new IllegalArgumentException();
		}
		entries = (LinkedEntry<K, V>[]) new LinkedEntry<?, ?>[initialCapacity];
		this.loadFactor = loadFactor;
	}

	public HashMap(Map<? extends K, ? extends V> m) {
		this(m.size() * 4 / 3, 0.75f); // TODO: check this factor
		putAll(m);
	}

	public int size() {
		return count;
	}

	private int bucketFor(Object key) {
		return Math.abs(Objects.hashCode(key)) % entries.length;
	}

	private LinkedEntry<K, V> entryFor(Object key) {
		LinkedEntry<K, V> ent = entries[bucketFor(key)];
		while (ent != null) {
			if (Objects.equals(ent.getKey(), key)) {
				return ent;
			}
			ent = ent.next;
		}
		return null;
	}

	private LinkedEntry<K, V> removeEntryFor(Object key) {
		int bucket = bucketFor(key);
		LinkedEntry<K, V> ent = entries[bucket];
		LinkedEntry<K, V> last = null;
		while (ent != null) {
			if (ent.getKey().equals(key)) {
				if (last == null) {
					entries[bucket] = ent.next;
				} else {
					last.next = ent.next;
				}
				return ent;
			}
			last = ent;
			ent = ent.next;
		}
		return null;
	}

	private void insertEntryFor(K key, V value) {
		int bucket = bucketFor(key);
		entries[bucket] = new LinkedEntry<K, V>(key, value, entries[bucket]);
	}

	private void increaseCapacity(int size) {
		LinkedEntry<K, V>[] old = entries;
		entries = (LinkedEntry<K, V>[]) new LinkedEntry<?, ?>[size];
		for (LinkedEntry<K, V> ent : old) {
			do {
				int bucket = bucketFor(ent.getKey());
				LinkedEntry<K, V> old_next = ent.next;
				ent.next = entries[bucket];
				entries[bucket] = ent;
				ent = old_next;
			} while (ent != null);
		}
	}

	public V get(Object key) {
		LinkedEntry<K, V> ent = entryFor(key);
		return ent == null ? null : ent.getValue();
	}

	public boolean containsKey(Object key) {
		return entryFor(key) != null;
	}

	public V put(K key, V value) {
		LinkedEntry<K, V> ent = entryFor(key);
		if (ent != null) {
			return ent.setValue(value);
		}
		if (count > loadFactor * entries.length) {
			increaseCapacity(entries.length * 2);
		}
		insertEntryFor(key, value);
		count++;
		return null;
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		int n = entries.length, expected = m.size();
		while (count + expected > loadFactor * n) {
			n *= 2;
		}
		if (n != entries.length) {
			increaseCapacity(n);
		}
		for (Map.Entry<? extends K, ? extends V> ent : m.entrySet()) {
			LinkedEntry<K, V> mine = entryFor(ent.getKey());
			if (mine != null) {
				mine.setValue(ent.getValue());
			} else {
				insertEntryFor(ent.getKey(), ent.getValue());
				count++;
			}
		}
	}

	public V remove(Object key) {
		LinkedEntry<K, V> ent = removeEntryFor(key);
		if (ent == null) {
			return null;
		} else {
			count--;
			return ent.getValue();
		}
	}

	public void clear() {
		for (int i = 0; i < entries.length; i++) {
			entries[i] = null;
		}
		count = 0;
	}

	public boolean containsValue(Object value) {
		for (LinkedEntry<K, V> ent : entries) {
			do {
				if (Objects.equals(value, ent.getValue())) {
					return true;
				}
				ent = ent.next;
			} while (ent != null);
		}
		return false;
	}

	public Object clone() {
		throw new IncompleteImplementationError();
	}

	public Set<Map.Entry<K, V>> entrySet() {
		if (setView == null) {
			setView = new SetView();
		}
		return setView;
	}
}
