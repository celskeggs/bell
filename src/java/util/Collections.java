package java.util;

import com.celskeggs.support.CUtil;

public class Collections {
	// TODO: implement all of this
	// TODO: serializable results
	public static final <K, V> Map<K, V> emptyMap() {
		return new Map<K, V>() {
			public void clear() {
				throw new UnsupportedOperationException("Immutable map");
			}

			public boolean containsKey(Object key) {
				return false;
			}

			public boolean containsValue(Object value) {
				return false;
			}

			public Set<java.util.Map.Entry<K, V>> entrySet() {
				return emptySet();
			}

			public V get(Object key) {
				return null;
			}

			public boolean isEmpty() {
				return true;
			}

			public Set<K> keySet() {
				return emptySet();
			}

			public V put(K key, V value) {
				throw new UnsupportedOperationException("Immutable map");
			}

			public void putAll(Map<? extends K, ? extends V> m) {
				throw new UnsupportedOperationException("Immutable map");
			}

			public V remove(Object key) {
				throw new UnsupportedOperationException("Immutable map");
			}

			public int size() {
				return 0;
			}

			public Collection<V> values() {
				return emptyList();
			}
		};
	}

	public static final <T> Set<T> emptySet() {
		return new AbstractSet<T>() {
			public boolean add(T e) {
				throw new UnsupportedOperationException("Immutable set");
			}

			public boolean contains(Object o) {
				return false;
			}

			public boolean containsAll(Collection<?> c) {
				return c.isEmpty();
			}

			public Iterator<T> iterator() {
				return emptyIterator();
			}

			public boolean remove(Object o) {
				throw new UnsupportedOperationException("Immutable set");
			}

			public int size() {
				return 0;
			}

			public Object[] toArray() {
				return new Object[0];
			}

			public <E> E[] toArray(E[] a) {
				if (a.length > 0) {
					a[0] = null;
				}
				return a;
			}
		};
	}

	public static <T> Iterator<T> emptyIterator() {
		return emptyListIterator();
	}

	public static <T> ListIterator<T> emptyListIterator() {
		return new ListIterator<T>() {
			public boolean hasNext() {
				return false;
			}

			public T next() {
				throw new NoSuchElementException();
			}

			public void remove() {
				throw new UnsupportedOperationException("Immutable iterator");
			}

			public void add(T e) {
				throw new UnsupportedOperationException("Immutable iterator");
			}

			public boolean hasPrevious() {
				return false;
			}

			public int nextIndex() {
				return 0;
			}

			public T previous() {
				throw new NoSuchElementException();
			}

			public int previousIndex() {
				return -1;
			}

			public void set(T e) {
				throw new UnsupportedOperationException("Immutable iterator");
			}
		};
	}

	public static final <T> List<T> emptyList() {
		return new List<T>() {
			public boolean add(T e) {
				throw new UnsupportedOperationException("Immutable list");
			}

			public boolean addAll(Collection<? extends T> c) {
				throw new UnsupportedOperationException("Immutable list");
			}

			public void clear() {
				throw new UnsupportedOperationException("Immutable list");
			}

			public boolean contains(Object o) {
				return false;
			}

			public boolean containsAll(Collection<?> c) {
				return c.isEmpty();
			}

			public boolean isEmpty() {
				return true;
			}

			public Iterator<T> iterator() {
				return emptyIterator();
			}

			public boolean remove(Object o) {
				throw new UnsupportedOperationException("Immutable list");
			}

			public boolean removeAll(Collection<?> c) {
				throw new UnsupportedOperationException("Immutable list");
			}

			public boolean retainAll(Collection<?> c) {
				throw new UnsupportedOperationException("Immutable list");
			}

			public int size() {
				return 0;
			}

			public Object[] toArray() {
				return new Object[0];
			}

			public <E> E[] toArray(E[] a) {
				if (a.length > 0) {
					a[0] = null;
				}
				return a;
			}

			public void add(int index, T element) {
				throw new UnsupportedOperationException("Immutable list");
			}

			public boolean addAll(int index, Collection<? extends T> c) {
				throw new UnsupportedOperationException("Immutable list");
			}

			public T get(int index) {
				throw new IndexOutOfBoundsException("List is empty");
			}

			public int indexOf(Object o) {
				return -1;
			}

			public int lastIndexOf(Object o) {
				return -1;
			}

			public ListIterator<T> listIterator() {
				return emptyListIterator();
			}

			public ListIterator<T> listIterator(int index) {
				if (index != 0) {
					throw new IndexOutOfBoundsException("Invalid index: " + index);
				}
				return emptyListIterator();
			}

			public T remove(int index) {
				throw new UnsupportedOperationException("Immutable list");
			}

			public T set(int index, T element) {
				throw new UnsupportedOperationException("Immutable list");
			}

			public List<T> subList(int fromIndex, int toIndex) {
				if (fromIndex != 0 || toIndex != 0) {
					throw new IndexOutOfBoundsException("Invalid indexes: " + fromIndex + ", " + toIndex);
				}
				return emptyList();
			}

		};
	}

	private Collections() {
	}

	private static <V, K> Map.Entry<K, V> wrapEnt(final Map.Entry<K, V> ent) {
		return new Map.Entry<K, V>() {
			public K getKey() {
				return (K) ent.getKey();
			}

			public V getValue() {
				return (V) ent.getValue();
			}

			public V setValue(V value) {
				throw new UnsupportedOperationException("Immutable map");
			}

			@Override
			public boolean equals(Object o) {
				return o instanceof Map.Entry && Objects.equals(getKey(), ((Map.Entry<?, ?>) o).getKey())
						&& Objects.equals(getValue(), ((Map.Entry<?, ?>) o).getValue());
			}
		};
	}

	public static <K, V> Map<K, V> unmodifiableMap(final Map<? extends K, ? extends V> m) {
		return new Map<K, V>() {
			public void clear() {
				throw new UnsupportedOperationException("Immutable map");
			}

			public boolean containsKey(Object key) {
				return m.containsKey(key);
			}

			public boolean containsValue(Object value) {
				return m.containsValue(value);
			}

			public Set<java.util.Map.Entry<K, V>> entrySet() {
				final Set<?> entrySet = m.entrySet();
				return new AbstractSet<Map.Entry<K, V>>() {
					public boolean add(java.util.Map.Entry<K, V> e) {
						throw new UnsupportedOperationException("Immutable map");
					}

					public boolean contains(Object o) {
						return entrySet.contains(o);
					}

					public boolean containsAll(Collection<?> c) {
						return entrySet.containsAll(c);
					}

					public boolean isEmpty() {
						return entrySet.isEmpty();
					}

					public Iterator<Map.Entry<K, V>> iterator() {
						final Iterator<?> i = entrySet.iterator();
						return new Iterator<Map.Entry<K, V>>() {
							public boolean hasNext() {
								return i.hasNext();
							}

							public Map.Entry<K, V> next() {
								return wrapEnt((Map.Entry<K, V>) i.next());
							}

							public void remove() {
								throw new UnsupportedOperationException("Immutable map");
							}
						};
					}

					public boolean remove(Object o) {
						throw new UnsupportedOperationException("Immutable map");
					}

					public int size() {
						return entrySet.size();
					}

					public Object[] toArray() {
						Object[] out = entrySet.toArray();
						for (int i = 0; i < out.length; i++) {
							out[i] = wrapEnt((Map.Entry<K, V>) out[i]);
						}
						return out;
					}

					public <T> T[] toArray(T[] a) {
						// TODO: check that this actually works
						Object[] out = entrySet.toArray();
						if (a.length < out.length) {
							a = CUtil.copyOfType(a, out.length);
						}
						for (int i = 0; i < out.length; i++) {
							a[i] = (T) wrapEnt((Map.Entry<K, V>) out[i]);
						}
						return a;
					}
				};
			}

			public V get(Object key) {
				return m.get(key);
			}

			public boolean isEmpty() {
				return m.isEmpty();
			}

			public Set<K> keySet() {
				return unmodifiableSet(m.keySet());
			}

			public V put(K key, V value) {
				throw new UnsupportedOperationException("Immutable map");
			}

			public void putAll(Map<? extends K, ? extends V> m) {
				throw new UnsupportedOperationException("Immutable map");
			}

			public V remove(Object key) {
				throw new UnsupportedOperationException("Immutable map");
			}

			public int size() {
				return m.size();
			}

			public Collection<V> values() {
				return unmodifiableCollection(m.values());
			}
		};
	}

	public static <T> Set<T> unmodifiableSet(final Set<? extends T> s) {
		return new AbstractSet<T>() {
			public boolean add(T e) {
				throw new UnsupportedOperationException("Immutable set");
			}

			public void clear() {
				throw new UnsupportedOperationException("Immutable set");
			}

			public boolean contains(Object o) {
				return s.contains(o);
			}

			public boolean containsAll(Collection<?> c) {
				return s.containsAll(c);
			}

			public boolean isEmpty() {
				return s.isEmpty();
			}

			public Iterator<T> iterator() {
				final Iterator<? extends T> i = s.iterator();
				return new Iterator<T>() {
					public boolean hasNext() {
						return i.hasNext();
					}

					public T next() {
						return i.next();
					}

					public void remove() {
						throw new UnsupportedOperationException("Immutable set");
					}
				};
			}

			public boolean remove(Object o) {
				throw new UnsupportedOperationException("Immutable set");
			}

			public int size() {
				return s.size();
			}

			public Object[] toArray() {
				return s.toArray();
			}

			public <E> E[] toArray(E[] a) {
				return s.toArray(a);
			}
		};
	}

	public static <T> Collection<T> unmodifiableCollection(final Collection<? extends T> c) {
		return new Collection<T>() {
			public boolean add(T e) {
				throw new UnsupportedOperationException("Immutable collection");
			}

			public boolean addAll(Collection<? extends T> c) {
				throw new UnsupportedOperationException("Immutable collection");
			}

			public void clear() {
				throw new UnsupportedOperationException("Immutable collection");
			}

			public boolean contains(Object o) {
				return c.contains(o);
			}

			public boolean containsAll(Collection<?> c) {
				return c.containsAll(c);
			}

			public boolean isEmpty() {
				return c.isEmpty();
			}

			public Iterator<T> iterator() {
				final Iterator<? extends T> i = c.iterator();
				return new Iterator<T>() {
					public boolean hasNext() {
						return i.hasNext();
					}

					public T next() {
						return i.next();
					}

					public void remove() {
						throw new UnsupportedOperationException("Immutable collection");
					}
				};
			}

			public boolean remove(Object o) {
				throw new UnsupportedOperationException("Immutable collection");
			}

			public boolean removeAll(Collection<?> c) {
				throw new UnsupportedOperationException("Immutable collection");
			}

			public boolean retainAll(Collection<?> c) {
				throw new UnsupportedOperationException("Immutable collection");
			}

			public int size() {
				return c.size();
			}

			public Object[] toArray() {
				return c.toArray();
			}

			public <E> E[] toArray(E[] a) {
				return c.toArray(a);
			}
		};
	}

	public static <T> List<T> unmodifiableList(final List<? extends T> list) {
		return new List<T>() {
			public boolean add(T e) {
				throw new UnsupportedOperationException("Immutable list");
			}

			public boolean addAll(Collection<? extends T> c) {
				throw new UnsupportedOperationException("Immutable list");
			}

			public void clear() {
				throw new UnsupportedOperationException("Immutable list");
			}

			public boolean contains(Object o) {
				return list.contains(o);
			}

			public boolean containsAll(Collection<?> c) {
				return c.containsAll(c);
			}

			public boolean isEmpty() {
				return list.isEmpty();
			}

			public Iterator<T> iterator() {
				final Iterator<? extends T> i = list.iterator();
				return new Iterator<T>() {
					public boolean hasNext() {
						return i.hasNext();
					}

					public T next() {
						return i.next();
					}

					public void remove() {
						throw new UnsupportedOperationException("Immutable list");
					}
				};
			}

			public boolean remove(Object o) {
				throw new UnsupportedOperationException("Immutable list");
			}

			public boolean removeAll(Collection<?> c) {
				throw new UnsupportedOperationException("Immutable list");
			}

			public boolean retainAll(Collection<?> c) {
				throw new UnsupportedOperationException("Immutable list");
			}

			public int size() {
				return list.size();
			}

			public Object[] toArray() {
				return list.toArray();
			}

			public <E> E[] toArray(E[] a) {
				return list.toArray(a);
			}

			public void add(int index, T element) {
				throw new UnsupportedOperationException("Immutable list");
			}

			public boolean addAll(int index, Collection<? extends T> c) {
				throw new UnsupportedOperationException("Immutable list");
			}

			public T get(int index) {
				return list.get(index);
			}

			public int indexOf(Object o) {
				return list.indexOf(o);
			}

			public int lastIndexOf(Object o) {
				return list.lastIndexOf(o);
			}

			public ListIterator<T> listIterator() {
				return listIterator(0);
			}

			public ListIterator<T> listIterator(int index) {
				final ListIterator<? extends T> base = list.listIterator(index);
				return new ListIterator<T>() {
					public void add(T e) {
						throw new UnsupportedOperationException("Immutable list");
					}

					public boolean hasNext() {
						return base.hasNext();
					}

					public boolean hasPrevious() {
						return base.hasPrevious();
					}

					public T next() {
						return base.next();
					}

					public int nextIndex() {
						return base.nextIndex();
					}

					public T previous() {
						return base.previous();
					}

					public int previousIndex() {
						return base.previousIndex();
					}

					public void remove() {
						throw new UnsupportedOperationException("Immutable list");
					}

					public void set(T e) {
						throw new UnsupportedOperationException("Immutable list");
					}
				};
			}

			public T remove(int index) {
				throw new UnsupportedOperationException("Immutable list");
			}

			public T set(int index, T element) {
				throw new UnsupportedOperationException("Immutable list");
			}

			public List<T> subList(int fromIndex, int toIndex) {
				return unmodifiableList(list.subList(fromIndex, toIndex));
			}
		};
	}

	public static <T> Comparator<T> reverseOrder(final Comparator<T> comparator) {
		return new Comparator<T>() {
			public int compare(T o1, T o2) {
				return -comparator.compare(o1, o2);
			}
		};
	}
}
