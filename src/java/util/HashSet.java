package java.util;

public class HashSet<E>
		extends AbstractSet<E> /* implements Cloneable, Serializable */ {

	private static final class LinkedEntry<E> {
		LinkedEntry<E> next;
		E value;

		public LinkedEntry(E value, LinkedEntry<E> next) {
			this.value = value;
			this.next = next;
		}
	}

	private LinkedEntry<E>[] entries;
	private final float loadFactor;
	private int count = 0;

	public HashSet() {
		this(16, 0.75f);
	}

	public HashSet(Collection<? extends E> c) {
		this(c.size() * 4 / 3, 0.75f); // TODO: check this factor
		addAll(c);
	}

	public HashSet(int initialCapacity) {
		this(initialCapacity, 0.75f);
	}

	public HashSet(int initialCapacity, float loadFactor) {
		if (initialCapacity < 0 || loadFactor <= 0) {
			throw new IllegalArgumentException();
		}
		entries = (LinkedEntry<E>[]) new LinkedEntry<?>[initialCapacity];
		this.loadFactor = loadFactor;
	}

	public Iterator<E> iterator() {
		return new Iterator<E>() {
			int i = 0;
			LinkedEntry<E> next;
			LinkedEntry<E> last = null;

			public boolean hasNext() {
				while (next == null) {
					if (i >= count) {
						return false;
					}
					next = entries[i++];
				}
				return true;
			}

			public E next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				last = next;
				next = next.next;
				return last.value;
			}

			public void remove() {
				if (last == null) {
					throw new IllegalStateException();
				}
				HashSet.this.remove(last.value);
				last = null;
			}
		};
	}

	public int size() {
		return count;
	}

	public boolean isEmpty() {
		return count == 0;
	}

	private int bucketFor(Object value) {
		return Math.abs(Objects.hashCode(value)) % entries.length;
	}

	public boolean contains(Object o) {
		LinkedEntry<E> ent = entries[bucketFor(o)];
		while (ent != null) {
			if (Objects.equals(ent.value, o)) {
				return true;
			}
			ent = ent.next;
		}
		return false;
	}

	private void increaseCapacity(int size) {
		LinkedEntry<E>[] old = entries;
		entries = (LinkedEntry<E>[]) new LinkedEntry<?>[size];
		for (LinkedEntry<E> ent : old) {
			do {
				int bucket = bucketFor(ent.value);
				LinkedEntry<E> old_next = ent.next;
				ent.next = entries[bucket];
				entries[bucket] = ent;
				ent = old_next;
			} while (ent != null);
		}
	}

	public boolean add(E e) {
		int bucket = bucketFor(e);
		LinkedEntry<E> ent = entries[bucket];
		while (ent != null) {
			if (Objects.equals(ent.value, e)) {
				return false;
			}
			ent = ent.next;
		}
		if (count > loadFactor * entries.length) {
			increaseCapacity(entries.length * 2);
		}
		entries[bucket] = new LinkedEntry<E>(e, entries[bucket]);
		count++;
		return true;
	}
	
	public boolean remove(Object o) {
		int bucket = bucketFor(o);
		LinkedEntry<E> ent = entries[bucket];
		LinkedEntry<E> last = null;
		while (ent != null) {
			if (Objects.equals(ent.value, o)) {
				if (last == null) { // first
					entries[bucket] = ent.next;
				} else {
					last.next = ent.next;
				}
				count--;
				return true;
			}
			last = ent;
			ent = ent.next;
		}
		return false;
	}
	
	public void clear() {
		for (int i = 0; i < entries.length; i++) {
			entries[i] = null;
		}
	}
	
	// public Object clone() TODO
}
