package java.util;

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
		this();
		// TODO: this needs to run in linear time
		this.putAll(m);
	}

	private final class Node implements Map.Entry<K, V> {
		Node parent, left, right;
		final K key;
		V value;

		public Node(K key, V value) {
			this.key = key;
			this.value = value;
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

		public V setValue(V value) {
			// TODO: WHHHYYYY?
			throw new UnsupportedOperationException("Cannot setValue on TreeMap");
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
		Node node = getNodeForKeyAndSplayIfNotFound(key);
		if (node == null) {
			return null;
		}
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
		return node.value;
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
		return current;
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
		return current;
	}

	public Map.Entry<K, V> pollFirstEntry() {
		Node current = root;
		if (current == null) {
			return null;
		}
		if (current.left == null) {
			size--;
			setRoot(current.right);
			return current;
		}
		Node last;
		do {
			last = current;
			current = current.left;
		} while (current.left != null);
		last.setLeft(current.right);
		// TODO: is this splay a good idea?
		splay(last);
		current.right = current.parent = null;
		return current;
	}
}
