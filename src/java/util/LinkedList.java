package java.util;

public class LinkedList<E> extends AbstractSequentialList<E>
		implements List<E>, Deque<E> /* , Cloneable, Serializable */ {

	// TODO: failfast iterators

	private final class Node {
		Node prev, next;
		E value; // MUST be null on the sentinel at all times!

		public Node(Node prev, E value, Node next) {
			this.prev = prev;
			this.value = value;
			this.next = next;
		}

		private Node prevCheck() {
			if (prev == sentinel) {
				throw new NoSuchElementException();
			}
			return prev;
		}

		private Node nextCheck() {
			if (next == sentinel) {
				throw new NoSuchElementException();
			}
			return next;
		}

		private E remove() {
			if (this == sentinel) {
				throw new IllegalStateException();
			}
			next.prev = prev;
			prev.next = next;
			size--;
			return value;
		}

		public void addAfter(E e) {
			next = next.prev = new Node(this, e, next);
			size++;
		}

		public void addBefore(E e) {
			prev = prev.next = new Node(prev, e, this);
			size++;
		}

		public boolean is(Object o) {
			if (this == sentinel) {
				throw new IllegalStateException();
			}
			if (o == null) {
				return value == null;
			} else {
				return o.equals(value);
			}
		}

		public E set(E element) {
			E old = value;
			value = element;
			return old;
		}
	}

	private Node getNodeAt(int index, boolean allowSentinel) {
		if (allowSentinel) {
			if (index < 0 || index > size) {
				throw new IndexOutOfBoundsException();
			}
		} else {
			if (index < 0 || index >= size) {
				throw new IndexOutOfBoundsException();
			}
		}
		if (index >= size / 2) {
			Node n = sentinel;
			for (int i = size; i > index; i--) {
				n = n.prev;
			}
			return n;
		} else {
			Node n = sentinel;
			for (int i = 0; i <= index; i++) {
				n = n.next;
			}
			return n;
		}
	}

	private final Node sentinel;
	private int size;

	public LinkedList() {
		sentinel = new Node(null, null, null);
		sentinel.next = sentinel.prev = sentinel;
		size = 0;
	}

	public LinkedList(Collection<? extends E> c) {
		this();
		addAll(c);
	}

	public E getFirst() {
		return sentinel.nextCheck().value;
	}

	public E getLast() {
		return sentinel.prevCheck().value;
	}

	public E removeFirst() {
		return sentinel.nextCheck().remove();
	}

	public E removeLast() {
		return sentinel.prevCheck().remove();
	}

	public void addFirst(E e) {
		sentinel.addAfter(e);
	}

	public void addLast(E e) {
		sentinel.addBefore(e);
	}

	@Override
	public boolean contains(Object o) {
		Node n = sentinel.next;
		while (n != sentinel) {
			if (n.is(o)) {
				return true;
			}
			n = n.next;
		}
		return false;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean add(E e) {
		addLast(e);
		return true;
	}

	@Override
	public boolean remove(Object o) {
		Node n = sentinel.next;
		while (n != sentinel) {
			if (n.is(o)) {
				n.remove();
				return true;
			}
			n = n.next;
		}
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean any = false;
		for (E e : c) {
			sentinel.addBefore(e);
			any = true;
		}
		return any;
	}

	@Override
	public void clear() {
		sentinel.next = sentinel.prev = sentinel;
		size = 0;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		Node n = getNodeAt(index, true);
		boolean any = false;
		for (E e : c) {
			any = true;
			n.addBefore(e);
		}
		return any;
	}

	@Override
	public E get(int index) {
		return getNodeAt(index, false).value;
	}

	@Override
	public E set(int index, E element) {
		return getNodeAt(index, false).set(element);
	}

	@Override
	public void add(int index, E element) {
		getNodeAt(index, true).addBefore(element);
	}

	@Override
	public E remove(int index) {
		return getNodeAt(index, false).remove();
	}

	@Override
	public int indexOf(Object o) {
		Node n = sentinel;
		for (int i = 0; i < size; i++) {
			n = n.next;
			if (n.is(o)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		Node n = sentinel;
		for (int i = size - 1; i >= 0; i--) {
			n = n.prev;
			if (n.is(o)) {
				return i;
			}
		}
		return -1;
	}

	public E peek() {
		// works properly because if the list is empty, the sentinel's value
		// (null) is returned
		return sentinel.next.value;
	}

	public E peekFirst() {
		return sentinel.next.value;
	}

	public E peekLast() {
		return sentinel.prev.value;
	}

	public E element() {
		return sentinel.nextCheck().value;
	}

	public E poll() {
		if (sentinel.next == sentinel) {
			return null;
		} else {
			return sentinel.next.remove();
		}
	}

	public E pollFirst() {
		if (sentinel.next == sentinel) {
			return null;
		} else {
			return sentinel.next.remove();
		}
	}

	public E pollLast() {
		if (sentinel.prev == sentinel) {
			return null;
		} else {
			return sentinel.prev.remove();
		}
	}

	public E remove() {
		return sentinel.nextCheck().remove();
	}

	public boolean offer(E e) {
		sentinel.addBefore(e);
		return true;
	}

	public boolean offerFirst(E e) {
		sentinel.addAfter(e);
		return true;
	}

	public boolean offerLast(E e) {
		sentinel.addBefore(e);
		return true;
	}

	public void push(E e) {
		sentinel.addAfter(e);
	}

	public E pop() {
		return sentinel.nextCheck().remove();
	}

	public boolean removeFirstOccurrence(Object o) {
		Node n = sentinel.next;
		while (n != sentinel) {
			if (n.is(o)) {
				n.remove();
				return true;
			}
			n = n.next;
		}
		return false;
	}

	public boolean removeLastOccurrence(Object o) {
		Node n = sentinel.prev;
		while (n != sentinel) {
			if (n.is(o)) {
				n.remove();
				return true;
			}
			n = n.prev;
		}
		return false;
	}

	public ListIterator<E> listIterator(final int ind) {
		final Node n = getNodeAt(ind, true);
		return new ListIterator<E>() {

			private int index = ind;
			private Node node = n;
			private Node removeTarget = null;

			public boolean hasNext() {
				return node != sentinel;
			}

			public boolean hasPrevious() {
				return node.prev != sentinel;
			}

			public E next() {
				if (node == sentinel) {
					throw new NoSuchElementException();
				}
				removeTarget = node;
				node = node.next;
				index++;
				return removeTarget.value;
			}

			public E previous() {
				if (node.prev == sentinel) {
					throw new NoSuchElementException();
				}
				node = node.prev;
				removeTarget = node;
				index--;
				return removeTarget.value;
			}

			public int nextIndex() {
				return index;
			}

			public int previousIndex() {
				return index - 1;
			}

			public void add(E e) {
				node.addBefore(e);
				removeTarget = null;
				index++;
			}

			public void remove() {
				if (removeTarget == null) {
					throw new IllegalStateException();
				}
				if (removeTarget == node) {
					node = node.next;
				} else {
					index--;
				}
				removeTarget.remove();
				removeTarget = null;
			}

			public void set(E e) {
				if (removeTarget == null) {
					throw new IllegalStateException();
				}
				removeTarget.set(e);
			}
		};
	}

	public Iterator<E> descendingIterator() {
		return new Iterator<E>() {
			private Node node = sentinel.prev;
			private Node removeTarget = null;

			public boolean hasNext() {
				return node != sentinel;
			}

			public E next() {
				if (node == sentinel) {
					throw new NoSuchElementException();
				}
				removeTarget = node;
				node = node.prev;
				return removeTarget.value;
			}

			public void remove() {
				if (removeTarget == null) {
					throw new IllegalStateException();
				}
				removeTarget.remove();
				removeTarget = null;
			}
		};
	}

	// public Object clone() TODO

	public Object[] toArray() {
		return new ArrayList<E>(this).toArray();
	}

	public <T> T[] toArray(T[] a) {
		return new ArrayList<E>(this).toArray(a);
	}
}
