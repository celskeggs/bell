package com.celskeggs.bell.support;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;

public class ReverseNavigableMap<K, V> implements NavigableMap<K, V> {

	private final NavigableMap<K, V> orig;
	private final Iterable<Map.Entry<K, V>> reverseIter;

	public ReverseNavigableMap(NavigableMap<K, V> map, Iterable<Map.Entry<K, V>> reverseIter) {
		this.orig = map;
		this.reverseIter = reverseIter;
	}

	public Comparator<? super K> comparator() {
		return Collections.reverseOrder(orig.comparator());
	}

	public Set<Map.Entry<K, V>> entrySet() {
		final Set<Map.Entry<K, V>> oset = orig.entrySet();
		return new AbstractSet<Map.Entry<K, V>>() {

			@Override
			public boolean add(java.util.Map.Entry<K, V> e) {
				throw new UnsupportedOperationException("Cannot add directly to entry sets.");
			}

			@Override
			public void clear() {
				orig.clear();
			}

			@Override
			public boolean contains(Object o) {
				return oset.contains(o);
			}

			@Override
			public boolean remove(Object o) {
				return oset.remove(o);
			}

			@Override
			public Iterator<java.util.Map.Entry<K, V>> iterator() {
				return reverseIter.iterator();
			}

			@Override
			public int size() {
				return orig.size();
			}

			public Object[] toArray() {
				Object[] out = oset.toArray();
				CUtil.reverseArray(out, out.length);
				return out;
			}

			public <T> T[] toArray(T[] a) {
				// note: this doesn't work with concurrent modification...
				T[] out = oset.toArray(a);
				CUtil.reverseArray(out, oset.size());
				return out;
			}
		};
	}

	public Collection<V> values() {
		final Collection<V> oc = orig.values();
		return new Collection<V>() {
			public boolean add(V e) {
				return oc.add(e);
			}

			public boolean addAll(Collection<? extends V> c) {
				return oc.addAll(c);
			}

			public void clear() {
				oc.clear();
			}

			public boolean contains(Object o) {
				return oc.contains(o);
			}

			public boolean containsAll(Collection<?> c) {
				return oc.containsAll(c);
			}

			public boolean isEmpty() {
				return oc.isEmpty();
			}

			public Iterator<V> iterator() {
				final Iterator<Map.Entry<K, V>> iter = reverseIter.iterator();
				return new Iterator<V>() {
					public boolean hasNext() {
						return iter.hasNext();
					}

					public V next() {
						return iter.next().getValue();
					}

					public void remove() {
						iter.remove();
					}
				};
			}

			public boolean remove(Object o) {
				return oc.remove(o);
			}

			public boolean removeAll(Collection<?> c) {
				return oc.removeAll(c);
			}

			public boolean retainAll(Collection<?> c) {
				return oc.retainAll(c);
			}

			public int size() {
				return oc.size();
			}

			public Object[] toArray() {
				Object[] out = oc.toArray();
				CUtil.reverseArray(out, out.length);
				return out;
			}

			public <T> T[] toArray(T[] a) {
				// note: this doesn't work with concurrent modification...
				T[] out = oc.toArray(a);
				CUtil.reverseArray(out, oc.size());
				return out;
			}
		};
	}

	public Set<K> keySet() {
		return orig.descendingKeySet();
	}

	public K firstKey() {
		return orig.lastKey();
	}

	public K lastKey() {
		return orig.firstKey();
	}

	public void clear() {
		orig.clear();
	}

	public boolean containsKey(Object key) {
		return orig.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return orig.containsValue(value);
	}

	public V get(Object key) {
		return orig.get(key);
	}

	public boolean isEmpty() {
		return orig.isEmpty();
	}

	public V put(K key, V value) {
		return orig.put(key, value);
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		orig.putAll(m);
	}

	public V remove(Object key) {
		return orig.remove(key);
	}

	public int size() {
		return orig.size();
	}

	public java.util.Map.Entry<K, V> ceilingEntry(K key) {
		return orig.floorEntry(key);
	}

	public K ceilingKey(K key) {
		return orig.floorKey(key);
	}

	public NavigableSet<K> descendingKeySet() {
		return orig.navigableKeySet();
	}

	public NavigableMap<K, V> descendingMap() {
		return orig;
	}

	public Map.Entry<K, V> firstEntry() {
		return orig.lastEntry();
	}

	public Map.Entry<K, V> floorEntry(K key) {
		return orig.ceilingEntry(key);
	}

	public K floorKey(K key) {
		return orig.floorKey(key);
	}

	public Map.Entry<K, V> higherEntry(K key) {
		return orig.lowerEntry(key);
	}

	public K higherKey(K key) {
		return orig.lowerKey(key);
	}

	public Map.Entry<K, V> lastEntry() {
		return orig.firstEntry();
	}

	public Map.Entry<K, V> lowerEntry(K key) {
		return orig.higherEntry(key);
	}

	public K lowerKey(K key) {
		return orig.higherKey(key);
	}

	public NavigableSet<K> navigableKeySet() {
		return orig.descendingKeySet();
	}

	public java.util.Map.Entry<K, V> pollFirstEntry() {
		return orig.pollLastEntry();
	}

	public java.util.Map.Entry<K, V> pollLastEntry() {
		return orig.pollFirstEntry();
	}

	public SortedMap<K, V> headMap(K toKey) {
		return headMap(toKey, false);
	}

	public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
		return orig.tailMap(toKey, inclusive).descendingMap();
	}

	public SortedMap<K, V> subMap(K fromKey, K toKey) {
		return subMap(fromKey, true, toKey, false);
	}

	public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
		return orig.subMap(toKey, toInclusive, fromKey, fromInclusive).descendingMap();
	}

	public SortedMap<K, V> tailMap(K fromKey) {
		return tailMap(fromKey, true);
	}

	public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
		return orig.tailMap(fromKey, inclusive).descendingMap();
	}

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
}
