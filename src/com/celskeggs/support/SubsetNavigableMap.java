package com.celskeggs.support;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

public class SubsetNavigableMap<K, V> extends AbstractMap<K, V> implements NavigableMap<K, V> {

	private final NavigableMap<K, V> orig;
	private final K fromKey;
	private final boolean fromExists;
	private final boolean fromInclusive;
	private final K toKey;
	private final boolean toExists;
	private final boolean toInclusive;

	public SubsetNavigableMap(NavigableMap<K, V> orig, K fromKey, boolean fromExists, boolean fromInclusive, K toKey,
			boolean toExists, boolean toInclusive) {
		if (!fromExists && !toExists) { // used as an assumption later on
			throw new IllegalArgumentException("Subsets need to be limited in SOME way!");
		}
		this.orig = orig;
		this.fromKey = fromKey;
		this.fromExists = fromExists;
		this.fromInclusive = fromInclusive;
		this.toKey = toKey;
		this.toExists = toExists;
		this.toInclusive = toInclusive;
	}

	public Comparator<? super K> comparator() {
		return orig.comparator();
	}
	
	private final Iterable<Map.Entry<K, V>> reverseEntryIter = new Iterable<Map.Entry<K, V>>() {
		public Iterator<Map.Entry<K, V>> iterator() {
			final Iterator<K> iter = navigableKeySet().descendingIterator();
			return new Iterator<Map.Entry<K, V>>() {
				public boolean hasNext() {
					return iter.hasNext();
				}

				public Map.Entry<K, V> next() {
					final K k = iter.next();
					final V v = orig.get(k);
					return new Map.Entry<K, V>() {
						public K getKey() {
							return k;
						}

						public V getValue() {
							return v;
						}

						public V setValue(V value) {
							throw new UnsupportedOperationException("Cannot mutate Map entries.");
						}
					};
				}

				public void remove() {
					iter.remove();
				}
			};
		}
	};

	public Set<Map.Entry<K, V>> entrySet() {
		final Set<Map.Entry<K, V>> origSet = orig.entrySet();
		final Set<K> keySet = this.keySet();
		return new AbstractSet<Map.Entry<K, V>>() {
			@Override
			public boolean add(java.util.Map.Entry<K, V> e) {
				throw new UnsupportedOperationException("Cannot add directly to entry sets.");
			}

			@Override
			public boolean contains(Object o) {
				if (o instanceof Map.Entry<?, ?>) {
					Map.Entry<?, ?> ent = (Map.Entry<?, ?>) o;
					return rangeIncludesKey((K) ent.getKey()) && origSet.contains(ent);
				} else {
					return false;
				}
			}

			@Override
			public boolean remove(Object o) {
				if (o instanceof Map.Entry<?, ?>) {
					Map.Entry<?, ?> ent = (Map.Entry<?, ?>) o;
					return rangeIncludesKey((K) ent.getKey()) && origSet.remove(ent);
				} else {
					return false;
				}
			}

			@Override
			public void clear() {
				keySet.clear();
			}

			@Override
			public Iterator<Map.Entry<K, V>> iterator() {
				final Iterator<K> iter = keySet.iterator();
				return new Iterator<Map.Entry<K, V>>() {
					public boolean hasNext() {
						return iter.hasNext();
					}

					public Map.Entry<K, V> next() {
						final K k = iter.next();
						final V v = orig.get(k);
						return new Map.Entry<K, V>() {
							public K getKey() {
								return k;
							}

							public V getValue() {
								return v;
							}

							public V setValue(V value) {
								throw new UnsupportedOperationException("Cannot mutate Map entries.");
							}
						};
					}

					public void remove() {
						iter.remove();
					}
				};
			}

			@Override
			public boolean isEmpty() {
				return keySet.isEmpty();
			}

			@Override
			public int size() {
				return keySet.size();
			}
		};
	}

	private boolean rangeIncludesKey(K key) {
		if (fromExists) {
			int cmp = orig.comparator().compare(key, fromKey);
			if (cmp < 0 || (cmp == 0 && !fromInclusive)) {
				return false;
			}
		}
		if (toExists) {
			int cmp = orig.comparator().compare(key, toKey);
			if (cmp > 0 || (cmp == 0 && !toInclusive)) {
				return false;
			}
		}
		return true;
	}

	public Map.Entry<K, V> firstEntry() {
		Map.Entry<K, V> ent = fromExists ? (fromInclusive ? orig.ceilingEntry(fromKey) : orig.higherEntry(fromKey))
				: orig.firstEntry();
		if (ent == null || !rangeIncludesKey(ent.getKey())) {
			return null;
		} else {
			return ent;
		}
	}

	public Map.Entry<K, V> lastEntry() {
		Map.Entry<K, V> ent = toExists ? (toInclusive ? orig.floorEntry(toKey) : orig.lowerEntry(toKey))
				: orig.lastEntry();
		if (ent == null || !rangeIncludesKey(ent.getKey())) {
			return null;
		} else {
			return ent;
		}
	}

	public K firstKey() {
		Map.Entry<K, V> ent = firstEntry();
		if (ent == null) {
			throw new NoSuchElementException();
		}
		return ent.getKey();
	}

	public K lastKey() {
		Map.Entry<K, V> ent = lastEntry();
		if (ent == null) {
			throw new NoSuchElementException();
		}
		return ent.getKey();
	}

	public NavigableSet<K> navigableKeySet() {
		if (!fromExists) { // just toExists
			return orig.navigableKeySet().headSet(toKey, toInclusive);
		} else if (!toExists) { // just fromExists
			return orig.navigableKeySet().tailSet(fromKey, fromInclusive);
		} else {
			return orig.navigableKeySet().subSet(fromKey, fromInclusive, toKey, toInclusive);
		}
	}

	public NavigableSet<K> descendingKeySet() {
		return navigableKeySet().descendingSet();
	}

	public Set<K> keySet() {
		return navigableKeySet();
	}

	public Collection<V> values() {
		final Set<K> keySet = this.keySet();
		return new AbstractCollection<V>() {
			public boolean add(V e) {
				throw new UnsupportedOperationException("Cannot add directly to value collections.");
			}

			public Iterator<V> iterator() {
				final Iterator<K> iter = keySet.iterator();
				return new Iterator<V>() {
					public boolean hasNext() {
						return iter.hasNext();
					}

					public V next() {
						return orig.get(iter.next());
					}

					public void remove() {
						iter.remove();
					}
				};
			}

			@Override
			public boolean isEmpty() {
				return keySet.isEmpty();
			}

			public int size() {
				return keySet.size();
			}

			public void clear() {
				keySet.clear();
			}
		};
	}

	public void clear() {
		keySet().clear();
	}

	public boolean containsKey(Object key) {
		return rangeIncludesKey((K) key) && orig.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return values().contains(value);
	}

	public V get(Object key) {
		return rangeIncludesKey((K) key) ? orig.get(key) : null;
	}

	public boolean isEmpty() {
		return keySet().isEmpty();
	}

	public V put(K key, V value) {
		if (rangeIncludesKey(key)) {
			return orig.put(key, value);
		} else {
			throw new IllegalArgumentException("Put outside of submap range!");
		}
	}

	public V remove(Object key) {
		return rangeIncludesKey((K) key) ? orig.remove(key) : null;
	}

	public int size() {
		return keySet().size();
	}

	public NavigableMap<K, V> descendingMap() {
		return new ReverseNavigableMap<K, V>(this, reverseEntryIter);
	}

	private K entToKey(Map.Entry<K, V> ent) {
		return ent == null ? null : ent.getKey();
	}

	public K ceilingKey(K key) {
		return entToKey(ceilingEntry(key));
	}

	public K floorKey(K key) {
		return entToKey(floorEntry(key));
	}

	public K higherKey(K key) {
		return entToKey(higherEntry(key));
	}

	public K lowerKey(K key) {
		return entToKey(lowerEntry(key));
	}

	public Map.Entry<K, V> ceilingEntry(K key) {
		Map.Entry<K, V> ent;
		if (fromExists && orig.comparator().compare(key, fromKey) <= 0) {
			if (fromInclusive) {
				ent = orig.ceilingEntry(fromKey);
			} else {
				ent = orig.higherEntry(fromKey);
			}
		} else {
			ent = orig.ceilingEntry(key);
		}
		return ent != null && rangeIncludesKey(ent.getKey()) ? ent : null;
	}

	public Map.Entry<K, V> floorEntry(K key) {
		Map.Entry<K, V> ent;
		if (toExists && orig.comparator().compare(key, toKey) >= 0) {
			if (toInclusive) {
				ent = orig.floorEntry(toKey);
			} else {
				ent = orig.lowerEntry(toKey);
			}
		} else {
			ent = orig.floorEntry(key);
		}
		return ent != null && rangeIncludesKey(ent.getKey()) ? ent : null;
	}

	public Map.Entry<K, V> higherEntry(K key) {
		Map.Entry<K, V> ent;
		if (fromExists && orig.comparator().compare(key, fromKey) < 0) {
			if (fromInclusive) {
				ent = orig.ceilingEntry(fromKey);
			} else {
				ent = orig.higherEntry(fromKey);
			}
		} else {
			ent = orig.higherEntry(key);
		}
		return ent != null && rangeIncludesKey(ent.getKey()) ? ent : null;
	}

	public Map.Entry<K, V> lowerEntry(K key) {
		Map.Entry<K, V> ent;
		if (toExists && orig.comparator().compare(key, toKey) > 0) {
			if (toInclusive) {
				ent = orig.floorEntry(toKey);
			} else {
				ent = orig.lowerEntry(toKey);
			}
		} else {
			ent = orig.lowerEntry(key);
		}
		return ent != null && rangeIncludesKey(ent.getKey()) ? ent : null;
	}

	public Map.Entry<K, V> pollFirstEntry() {
		Map.Entry<K, V> ent = firstEntry();
		if (ent != null) {
			orig.remove(ent.getKey());
		}
		return ent;
	}

	public Map.Entry<K, V> pollLastEntry() {
		Map.Entry<K, V> ent = lastEntry();
		if (ent != null) {
			orig.remove(ent.getKey());
		}
		return ent;
	}

	public SortedMap<K, V> tailMap(K fromKey) {
		return tailMap(fromKey, true);
	}

	public SortedMap<K, V> headMap(K toKey) {
		return headMap(toKey, false);
	}

	public SortedMap<K, V> subMap(K fromKey, K toKey) {
		return subMap(fromKey, true, toKey, false);
	}

	public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
		if (!rangeIncludesKey(fromKey)) {
			throw new IllegalArgumentException("Can only calculate submaps, not supermaps!");
		}
		return new SubsetNavigableMap<K, V>(orig, fromKey, true, inclusive, toKey, toExists, toInclusive);
	}

	public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
		if (!rangeIncludesKey(toKey)) {
			throw new IllegalArgumentException("Can only calculate submaps, not supermaps!");
		}
		return new SubsetNavigableMap<K, V>(orig, fromKey, fromExists, fromInclusive, toKey, true, inclusive);
	}

	public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
		if (!rangeIncludesKey(fromKey) || !rangeIncludesKey(toKey)) {
			throw new IllegalArgumentException("Can only calculate submaps, not supermaps!");
		}
		return new SubsetNavigableMap<K, V>(orig, fromKey, true, fromInclusive, toKey, true, toInclusive);
	}
}
