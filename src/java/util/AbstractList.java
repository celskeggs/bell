package java.util;

public abstract class AbstractList<E> extends AbstractCollection<E> implements List<E> {
	private class AbstractIterator implements ListIterator<E> {
		int i = 0;
		int mutable = 0; // 0: not removable/addable, 1: removable/addable; last
							// was next(), -1: removable/addable; last was
							// previous()

		public AbstractIterator(int index) {
			i = index;
		}

		public boolean hasNext() {
			return i < AbstractList.this.size();
		}

		public boolean hasPrevious() {
			return i > 0;
		}

		public E next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			mutable = 1;
			return AbstractList.this.get(i++);
		}

		public E previous() {
			if (!hasPrevious()) {
				throw new NoSuchElementException();
			}
			mutable = -1;
			return AbstractList.this.get(--i);
		}

		public int nextIndex() {
			return i;
		}

		public int previousIndex() {
			return i - 1;
		}

		public void remove() {
			if (mutable > 0) {
				AbstractList.this.remove(--i);
				mutable = 0;
			} else if (mutable == 0) {
				throw new IllegalStateException();
			} else {
				AbstractList.this.remove(i);
				mutable = 0;
			}
		}

		public void set(E e) {
			if (mutable > 0) {
				AbstractList.this.set(i - 1, e);
			} else if (mutable == 0) {
				throw new IllegalStateException();
			} else {
				AbstractList.this.set(i, e);
			}
		}

		public void add(E e) {
			AbstractList.this.add(i, e);
			i += 1;
			mutable = 0;
		}
	}

	// protected transient int modCount; - TODO: modCount

	protected AbstractList() {
	}

	public boolean add(E e) {
		add(size(), e);
		return true;
	}

	public abstract E get(int index);

	public E set(int index, E element) {
		throw new UnsupportedOperationException("List is immutable.");
	}

	public void add(int index, E element) {
		throw new UnsupportedOperationException("List is immutable.");
	}

	public E remove(int index) {
		throw new UnsupportedOperationException("List is immutable.");
	}

	public int indexOf(Object o) {
		ListIterator<E> iterator = this.listIterator();
		int i = 0;
		while (iterator.hasNext()) {
			E e = iterator.next();
			if (Objects.equals(o, e)) {
				return i;
			}
			i++;
		}
		return -1;
	}

	public int lastIndexOf(Object o) {
		int size = size();
		ListIterator<E> iterator = this.listIterator(size);
		int i = size;
		while (iterator.hasPrevious()) {
			E e = iterator.previous();
			i--;
			if (Objects.equals(o, e)) {
				return i;
			}
		}
		return -1;
	}

	public void clear() {
		removeRange(0, size());
	}

	public boolean addAll(int index, Collection<? extends E> c) {
		boolean changed = false;
		for (E e : c) {
			add(index++, e);
			changed = true;
		}
		return changed;
	}

	public Iterator<E> iterator() {
		return listIterator();
	}

	public ListIterator<E> listIterator() {
		return listIterator(0);
	}

	public ListIterator<E> listIterator(int index) {
		if (index < 0 || index > size()) {
			throw new IndexOutOfBoundsException();
		}
		return new AbstractIterator(index);
	}

	public List<E> subList(int fromIndex, int toIndex) {
		throw new IncompleteImplementationError(); // TODO
	}

	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (!(o instanceof List)) {
			return false;
		} else {
			List<?> l = (List<?>) o;
			if (l.size() != size()) {
				return false;
			}
			Iterator<E> me = this.iterator();
			Iterator<?> them = l.iterator();
			while (me.hasNext()) {
				if (!them.hasNext()) {
					return false;
				}
				if (!Objects.equals(me.next(), them.next())) {
					return false;
				}
			}
			return !them.hasNext();
		}
	}

	public int hashCode() {
		int hashCode = 1;
		for (E e : this) {
			hashCode = 31 * hashCode + Objects.hashCode(e);
		}
		return hashCode;
	}

	protected void removeRange(int fromIndex, int toIndex) {
		int count = toIndex - fromIndex;
		if (count > 0) {
			ListIterator<E> iterator = listIterator(fromIndex);
			while (count > 0) {
				iterator.next();
				iterator.remove();
				count--;
			}
		}
	}
}
