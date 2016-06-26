package java.util;

import com.celskeggs.support.ReverseNavigableMap;
import com.celskeggs.support.SubsetNavigableMap;

// Unlike the Java SE implementation, this uses Splay Trees, not Red-Black Trees.
// TODO: THIS NEEDS TESTING
public class TreeMap<K, V> extends AbstractMap<K, V> implements NavigableMap<K, V>, Cloneable /* , Serializable */ {

	private final Comparator<? super K> comparator;

	public TreeMap() {
		this.comparator = new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				if (o1 == null || o2 == null) {
					throw new NullPointerException();
				}
				return ((Comparable<Object>) o1).compareTo(o2);
			}
		};
	}

	public TreeMap(Comparator<? super K> comparator) {
		this.comparator = comparator;
	}

	public TreeMap(Map<? extends K, ? extends V> m) {
		this();
		this.putAll(m);
	}

	public TreeMap(SortedMap<K, ? extends V> m) {
		this(m.comparator());
		// TODO: this needs to run in linear time
		this.putAll(m);
	}

	private final class KeySetImpl extends AbstractSet<K> implements NavigableSet<K> {

		private final KeySetImpl rev;
		private final boolean reversed;
		private final K beginKey, endKey;
		private final boolean hasBegin, includeBegin, hasEnd, includeEnd;

		private KeySetImpl(KeySetImpl base, K beginKey, boolean hasBegin, boolean includeBegin, K endKey,
				boolean hasEnd, boolean includeEnd) {
			this.reversed = base.reversed;
			this.beginKey = beginKey;
			this.endKey = endKey;
			this.includeBegin = includeBegin;
			this.includeEnd = includeEnd;
			this.hasBegin = hasBegin;
			this.hasEnd = hasEnd;
			this.rev = new KeySetImpl(this);
		}

		private KeySetImpl() { // forward set
			this.reversed = false;
			beginKey = endKey = null;
			includeBegin = includeEnd = hasBegin = hasEnd = false;
			this.rev = new KeySetImpl(this);
		}

		private KeySetImpl(KeySetImpl rev) {
			this.rev = rev;
			reversed = !rev.reversed;
			beginKey = rev.endKey;
			endKey = rev.beginKey;
			includeBegin = rev.includeEnd;
			includeEnd = rev.includeBegin;
			hasBegin = rev.hasEnd;
			hasEnd = rev.hasBegin;
		}

		@Override
		public Iterator<K> iterator() {
			// TODO: should the iterator splay?
			// TODO: make sure we don't overrun the end key, which I think we currently DO
			Node first = root;
			if (first != null) {
				if (hasBegin) {
					first = relativeEntry(beginKey, true, includeBegin);
				} else {
					if (reversed) {
						while (first.right != null) {
							first = first.right;
						}
					} else {
						while (first.left != null) {
							first = first.left;
						}
					}
				}
			}
			final Node firstf = first;
			return new Iterator<K>() {
				private Node last = null;
				private Node n = firstf;

				public boolean hasNext() {
					return n != null;
				}

				public K next() {
					if (n == null) {
						throw new NoSuchElementException();
					}
					last = n;
					if (reversed) {
						if (n.left != null) {
							n = n.left;
							while (n.right != null) {
								n = n.right;
							}
						} else {
							while (n.parent != null && n.parent.left == n) {
								n = n.parent;
							}
							n = n.parent;
						}
					} else {
						if (n.right != null) {
							n = n.right;
							while (n.left != null) {
								n = n.left;
							}
						} else {
							while (n.parent != null && n.parent.right == n) {
								n = n.parent;
							}
							n = n.parent;
						}
					}
					return last.key;
				}

				public void remove() {
					if (last == null) {
						throw new IllegalStateException();
					}
					removeNode(last);
					last = null;
				}
			};
		}
		
		@Override
		public boolean isEmpty() {
			if (TreeMap.this.isEmpty()) {
				return true;
			} else if (!hasBegin && !hasEnd) {
				return false;
			} else {
				// no way except to iterate
				return !iterator().hasNext();
			}
		}

		@Override
		public int size() {
			if (!hasBegin && !hasEnd) {
				return TreeMap.this.size();
			} else {
				// no way except to iterate
				int count = 0;
				for (K _ : this) {
					count++;
				}
				return count;
			}
		}

		public boolean add(K e) {
			throw new UnsupportedOperationException("Cannot add directly to key sets.");
		}

		public boolean remove(Object o) {
			return TreeMap.this.removeAndReturnNode(o) != null;
		}

		public boolean removeAll(Collection<?> c) {
			boolean mod = false;
			for (Object o : c) {
				mod |= remove(o);
			}
			return mod;
		}

		@Override
		public boolean contains(Object o) {
			return TreeMap.this.containsKey(o);
		}

		public Comparator<? super K> comparator() {
			return comparator;
		}

		public K first() {
			return firstKey();
		}

		public K last() {
			return lastKey();
		}

		public K ceiling(K e) {
			return ceilingKey(e);
		}

		public K floor(K e) {
			return floorKey(e);
		}

		public K higher(K e) {
			return higherKey(e);
		}

		public K lower(K e) {
			return lowerKey(e);
		}

		public Iterator<K> descendingIterator() {
			return rev.iterator();
		}

		public NavigableSet<K> descendingSet() {
			return rev;
		}

		public K pollFirst() {
			Map.Entry<K, V> ent = pollFirstEntry();
			return ent == null ? null : ent.getKey();
		}

		public K pollLast() {
			Map.Entry<K, V> ent = pollLastEntry();
			return ent == null ? null : ent.getKey();
		}

		public NavigableSet<K> headSet(K toElement) {
			return headSet(toElement, false);
		}

		public NavigableSet<K> tailSet(K fromElement) {
			return tailSet(fromElement, true);
		}

		public NavigableSet<K> subSet(K fromElement, K toElement) {
			return subSet(fromElement, true, toElement, false);
		}

		public NavigableSet<K> headSet(K toElement, boolean inclusive) {
			return new KeySetImpl(this, null, false, false, toElement, true, inclusive);
		}

		public NavigableSet<K> subSet(K fromElement, boolean fromInclusive, K toElement, boolean toInclusive) {
			return new KeySetImpl(this, fromElement, true, fromInclusive, toElement, true, toInclusive);
		}

		public NavigableSet<K> tailSet(K fromElement, boolean inclusive) {
			return new KeySetImpl(this, fromElement, true, inclusive, null, false, false);
		}
	}

	private final class Node {
		Node parent, left, right;
		final K key;
		V value;

		public Node(K key, V value) {
			this.key = key;
			this.value = value;
		}

		public int hashCode() {
			return Objects.hashCode(key) ^ Objects.hashCode(value);
		}

		public boolean equals(Object o) {
			return o instanceof Map.Entry && Objects.equals(key, ((Map.Entry<?, ?>) o).getKey())
					&& Objects.equals(value, ((Map.Entry<?, ?>) o).getValue());
		}

		public boolean isRoot() {
			if (parent == null) {
				assert this == root;
				return true;
			} else {
				assert this != root;
				return false;
			}
		}

		public boolean isLeft() {
			if (parent.left == this) {
				assert parent.right != this;
				return true;
			} else {
				assert parent.right == this;
				return false;
			}
		}

		public Node setLeft(Node new_left) {
			Node old = left;
			left = new_left;
			if (new_left != null) {
				new_left.parent = this;
			}
			return old;
		}

		public Node setRight(Node new_right) {
			Node old = right;
			right = new_right;
			if (new_right != null) {
				new_right.parent = this;
			}
			return old;
		}

		public void laceLR(Node o) {
			this.setLeft(o.setRight(this));
		}

		public void laceRL(Node o) {
			this.setRight(o.setLeft(this));
		}

		public K getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}

		public Map.Entry<K, V> toEntry() {
			final K key = this.key;
			final V value = this.value;
			return new Map.Entry<K, V>() {

				public K getKey() {
					return key;
				}

				public V getValue() {
					return value;
				}

				public V setValue(V value) {
					throw new UnsupportedOperationException("Cannot setValue on TreeMap");
				}
			};
		}
	}

	private Node root = null;
	private int size = 0;

	private void splay(Node node) {
		while (!node.isRoot()) {
			if (node.parent.isRoot()) {
				zig(node);
			} else {
				// zig-zig or zig-zag
				zigz(node);
			}
		}
	}

	private void zig(Node x) {
		Node y = x.parent;
		if (x.isLeft()) {
			y.laceLR(y);
		} else {
			y.laceRL(x);
		}
		setRoot(x);
	}

	private void setRoot(Node x) {
		root = x;
		x.parent = null;
	}

	private void zigz(Node x) {
		Node y = x.parent;
		Node z = y.parent;
		Node zp = z.parent;
		if (x.isLeft()) {
			if (y.isLeft()) {
				z.laceLR(y);
			} else {
				z.laceRL(x);
			}
			y.laceLR(x);
		} else {
			if (y.isLeft()) {
				z.laceLR(x);
			} else {
				z.laceRL(y);
			}
			y.laceRL(x);
		}
		if (z == root) {
			setRoot(x);
		} else if (zp.left == z) {
			zp.setLeft(x);
		} else {
			zp.setRight(x);
		}
	}

	@Override
	public int size() {
		return size;
	}

	private Node getNodeForKeyAndSplayIfNotFound(Object key) {
		Node current = root;
		Node last = null;
		while (current != null) {
			int compare = comparator.compare((K) key, current.key);
			if (compare == 0) {
				return current;
			}
			last = current;
			current = compare < 0 ? current.left : current.right;
		}
		if (last != null) {
			splay(last);
		}
		return null;
	}

	private Node getAndSplayNodeForKey(Object key) {
		Node current = root;
		Node last = null;
		while (current != null) {
			int compare = comparator.compare((K) key, current.key);
			if (compare == 0) {
				splay(current);
				return current;
			}
			last = current;
			current = compare < 0 ? current.left : current.right;
		}
		if (last != null) {
			splay(last);
		}
		return null;
	}

	@Override
	public boolean containsKey(Object key) {
		return getAndSplayNodeForKey(key) != null;
	}

	@Override
	public V get(Object key) {
		Node node = getAndSplayNodeForKey(key);
		return node == null ? null : node.value;
	}

	public Comparator<? super K> comparator() {
		return comparator;
	}

	@Override
	public V put(K key, V value) {
		Node current = root;
		if (current == null) {
			setRoot(new Node(key, value));
			size++;
			return null;
		}
		while (true) {
			int compare = comparator.compare((K) key, current.key);
			if (compare == 0) {
				splay(current);
				V lv = current.value;
				current.value = value;
				return lv;
			}
			Node last = current;
			current = compare < 0 ? current.left : current.right;
			if (current == null) {
				Node kn = new Node(key, value);
				size++;
				if (compare < 0) {
					last.setLeft(kn);
				} else {
					last.setRight(kn);
				}
				splay(kn);
				return null;
			}
		}
	}

	@Override
	public V remove(Object key) {
		Node n = removeAndReturnNode(key);
		return n == null ? null : n.value;
	}

	private Node removeAndReturnNode(Object key) {
		Node node = getNodeForKeyAndSplayIfNotFound(key);
		if (node != null) {
			removeNode(node);
		}
		return node;
	}

	private void removeNode(Node node) {
		Node repn;
		if (node.left != null) {
			if (node.right != null) {
				// first, get the largest key on our left
				if (node.left.right == null) {
					// shortcut: if there's a blank space there for our right
					// arm, just squash them together
					node.left.setRight(node.right);
					repn = node.left;
				} else {
					Node iter = node.left;
					Node ip;
					do {
						ip = iter;
						iter = iter.right;
					} while (iter.right != null);
					// we know that ip.right == iter, because of how we got here
					// replace iter with its left branch, if it exists. right
					// branch is guaranteed not to exist.
					ip.setRight(iter.left);
					iter.setLeft(node.left);
					iter.setRight(node.right);
					// and then replace node with iter
					repn = iter;
				}
			} else {
				repn = node.left;
			}
		} else {
			repn = node.right;
		}
		Node np = node.parent;
		if (np.left == node) {
			np.setLeft(repn);
		} else {
			np.setRight(repn);
		}
		size--;
		splay(np);
	}

	@Override
	public void clear() {
		size = 0;
		root = null;
	}

	public Object clone() throws CloneNotSupportedException {
		throw new IncompleteImplementationError();
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

	public Map.Entry<K, V> firstEntry() {
		Node current = root;
		if (current == null) {
			return null;
		}
		while (current.left != null) {
			current = current.left;
		}
		// TODO: is this splay a good idea?
		splay(current);
		return current.toEntry();
	}

	public Map.Entry<K, V> lastEntry() {
		Node current = root;
		if (current == null) {
			return null;
		}
		while (current.right != null) {
			current = current.right;
		}
		// TODO: is this splay a good idea?
		splay(current);
		return current.toEntry();
	}

	public Map.Entry<K, V> pollFirstEntry() {
		Node current = root;
		if (current == null) {
			return null;
		}
		if (current.left == null) {
			size--;
			setRoot(current.right);
			return current.toEntry();
		}
		Node last;
		do {
			last = current;
			current = current.left;
		} while (current.left != null);
		last.setLeft(current.right);
		// TODO: is this splay a good idea?
		splay(last);
		return current.toEntry();
	}

	public Map.Entry<K, V> pollLastEntry() {
		Node current = root;
		if (current == null) {
			return null;
		}
		if (current.right == null) {
			size--;
			setRoot(current.left);
			return current.toEntry();
		}
		Node last;
		do {
			last = current;
			current = current.right;
		} while (current.right != null);
		last.setRight(current.left);
		// TODO: is this splay a good idea?
		splay(last);
		return current.toEntry();
	}

	// TODO: this needs thorough testing
	private Node relativeEntry(K key, boolean greater, boolean equal) {
		Node current = root;
		if (current == null) {
			return null;
		}
		Node best = null, last;
		do {
			last = current;
			int cmp = comparator.compare(current.key, key);
			if ((greater ? -cmp : cmp) < 0 || (cmp == 0 && equal)) {
				if (best == null || comparator.compare(best.key, current.key) < 0) {
					best = current;
				}
			}
			if (cmp < 0 || (cmp == 0 && (equal ^ greater))) {
				current = current.right;
			} else {
				current = current.left;
			}
		} while (current != null);
		splay(best != null ? best : last); // TODO: good splay?
		return best;
	}

	public Map.Entry<K, V> lowerEntry(K key) {
		return relativeEntry(key, false, false).toEntry();
	}

	public K lowerKey(K key) {
		Map.Entry<K, V> ent = lowerEntry(key);
		return ent == null ? null : ent.getKey();
	}

	public Map.Entry<K, V> floorEntry(K key) {
		return relativeEntry(key, false, true).toEntry();
	}

	public K floorKey(K key) {
		Map.Entry<K, V> ent = floorEntry(key);
		return ent == null ? null : ent.getKey();
	}

	public Map.Entry<K, V> ceilingEntry(K key) {
		return relativeEntry(key, true, true).toEntry();
	}

	public K ceilingKey(K key) {
		Map.Entry<K, V> ent = ceilingEntry(key);
		return ent == null ? null : ent.getKey();
	}

	public Map.Entry<K, V> higherEntry(K key) {
		return relativeEntry(key, true, false).toEntry();
	}

	public K higherKey(K key) {
		Map.Entry<K, V> ent = higherEntry(key);
		return ent == null ? null : ent.getKey();
	}

	private final NavigableSet<K> keySet = new KeySetImpl();

	private Iterator<Map.Entry<K, V>> beginEntryIteration(final boolean reverse) {
		Node first = root;
		if (first != null) {
			if (reverse) {
				while (first.right != null) {
					first = first.right;
				}
			} else {
				while (first.left != null) {
					first = first.left;
				}
			}
		}
		final Node firstf = first;
		return new Iterator<Map.Entry<K, V>>() {
			private Node last = null;
			private Node n = firstf;

			public boolean hasNext() {
				return n != null;
			}

			public Map.Entry<K, V> next() {
				if (n == null) {
					throw new NoSuchElementException();
				}
				last = n;
				if (reverse) {
					if (n.left != null) {
						n = n.left;
						while (n.right != null) {
							n = n.right;
						}
					} else {
						while (n.parent != null && n.parent.left == n) {
							n = n.parent;
						}
						n = n.parent;
					}
				} else {
					if (n.right != null) {
						n = n.right;
						while (n.left != null) {
							n = n.left;
						}
					} else {
						while (n.parent != null && n.parent.right == n) {
							n = n.parent;
						}
						n = n.parent;
					}
				}
				return last.toEntry();
			}

			public void remove() {
				if (last == null) {
					throw new IllegalStateException();
				}
				removeNode(last);
				last = null;
			}
		};
	}

	private final Iterable<Map.Entry<K, V>> entrySetRevIterator = new Iterable<Map.Entry<K, V>>() {
		public Iterator<java.util.Map.Entry<K, V>> iterator() {
			return beginEntryIteration(true);
		}
	};

	private final Set<Map.Entry<K, V>> entrySet = new AbstractSet<Map.Entry<K, V>>() {
		public boolean add(java.util.Map.Entry<K, V> e) {
			throw new UnsupportedOperationException("Cannot add directly to entry sets.");
		}

		public void clear() {
			TreeMap.this.clear();
		}

		public boolean contains(Object o) {
			// TODO: (here and elsewhere) what if it IS a Map.Entry, but they key isn't valid here?
			if (o instanceof Map.Entry<?, ?>) {
				Map.Entry<?, ?> ent = (Map.Entry<?, ?>) o;
				Node nodeForKey = getAndSplayNodeForKey(ent.getKey());
				return nodeForKey != null && Objects.equals(ent.getValue(), nodeForKey.getValue());
			} else {
				return false;
			}
		}

		public boolean remove(Object o) {
			if (o instanceof Map.Entry<?, ?>) {
				Map.Entry<?, ?> ent = (Map.Entry<?, ?>) o;
				Node nodeForKey = getAndSplayNodeForKey(ent.getKey());
				if (nodeForKey != null && Objects.equals(ent.getValue(), nodeForKey.getValue())) {
					removeNode(nodeForKey);
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}

		public Iterator<Map.Entry<K, V>> iterator() {
			return beginEntryIteration(false);
		}

		public int size() {
			return TreeMap.this.size();
		}
	};

	private final Collection<V> valueColl = new AbstractCollection<V>() {
		public boolean add(V e) {
			throw new UnsupportedOperationException("Cannot add directly to value sets.");
		}

		public void clear() {
			TreeMap.this.clear();
		}

		public boolean contains(Object o) {
			return containsValue(o);
		}

		public Iterator<V> iterator() {
			final Iterator<Map.Entry<K, V>> ents = entrySet.iterator();
			return new Iterator<V>() {
				public boolean hasNext() {
					return ents.hasNext();
				}

				public V next() {
					return ents.next().getValue();
				}

				public void remove() {
					ents.remove();
				}
			};
		}

		public int size() {
			return TreeMap.this.size();
		}
	};

	private final NavigableMap<K, V> descending = new ReverseNavigableMap<K, V>(this, entrySetRevIterator);

	@Override
	public Set<K> keySet() {
		return keySet;
	}

	public NavigableSet<K> navigableKeySet() {
		return keySet;
	}

	public NavigableSet<K> descendingKeySet() {
		return keySet.descendingSet();
	}

	public Collection<V> values() {
		return valueColl;
	}

	public Set<Map.Entry<K, V>> entrySet() {
		return entrySet;
	}

	public NavigableMap<K, V> descendingMap() {
		return descending;
	}

	public SortedMap<K, V> subMap(K fromKey, K toKey) {
		return subMap(fromKey, true, toKey, false);
	}

	public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
		return new SubsetNavigableMap<K, V>(this, fromKey, true, fromInclusive, toKey, true, toInclusive);
	}

	public SortedMap<K, V> headMap(K toKey) {
		return headMap(toKey, false);
	}

	public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
		return new SubsetNavigableMap<K, V>(this, null, false, false, toKey, true, inclusive);
	}

	public SortedMap<K, V> tailMap(K fromKey) {
		return tailMap(fromKey, true);
	}

	public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
		return new SubsetNavigableMap<K, V>(this, fromKey, true, inclusive, null, false, false);
	}
}
