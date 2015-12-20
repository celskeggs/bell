package java.util;

import java.lang.reflect.Array;

import com.colbyskeggs.support.CUtil;

public class ArrayList<E> extends AbstractList<E>
		implements List<E>, RandomAccess /* , Cloneable, Serializable */ {

	private Object[] contents;
	private int length = 0;

	public ArrayList() {
		this(10);
	}

	// TODO: modcount handling

	public ArrayList(int initialCapacity) {
		if (initialCapacity < 0) {
			throw new IllegalArgumentException("Negative initial capacity!");
		}
		contents = new Object[initialCapacity];
	}

	public ArrayList(Collection<? extends E> c) {
		this(c.size() + 10);
		int i = 0;
		int clen = contents.length;
		for (E e : c) {
			if (i >= clen) {
				ensureCapacity(Math.max(c.size(), i) + 10);
				clen = contents.length;
			}
			contents[i++] = e;
		}
		length = i;
	}

	public void trimToSize() {
		contents = Arrays.copyOf(contents, length);
	}

	public void ensureCapacity(int minCapacity) {
		if (minCapacity > contents.length) {
			contents = Arrays.copyOf(contents, Math.max(minCapacity, length << 1));
		}
	}

	public int size() {
		return length;
	}

	public boolean contains(Object o) {
		for (int i = 0; i < length; i++) {
			if (Objects.equals(contents[i], o)) {
				return true;
			}
		}
		return false;
	}

	public int indexOf(Object o) {
		for (int i = 0; i < length; i++) {
			if (Objects.equals(contents[i], o)) {
				return i;
			}
		}
		return -1;
	}

	public int lastIndexOf(Object o) {
		for (int i = length - 1; i >= 0; i--) {
			if (Objects.equals(contents[i], o)) {
				return i;
			}
		}
		return -1;
	}

	public Object clone() {
		throw new IncompleteImplementationError();
	}

	public Object[] toArray() {
		return Arrays.copyOf(contents, length);
	}

	public <T> T[] toArray(T[] a) {
		T[] n = CUtil.copyOfType(a, length);
		System.arraycopy(contents, 0, n, 0, length);
		return n;
	}

	public E get(int index) {
		if (index < 0 || index >= size()) {
			throw new IndexOutOfBoundsException();
		}
		return (E) contents[index];
	}

	public E set(int index, E element) {
		if (index < 0 || index >= size()) {
			throw new IndexOutOfBoundsException();
		}
		E old = (E) contents[index];
		contents[index] = element;
		return old;
	}

	public boolean add(E e) {
		// TODO: ensure the proper level of threadsafety
		ensureCapacity(length + 1);
		contents[length++] = e;
		return true;
	}

	private void expandBubble(int index, int count) {
		ensureCapacity(length + count);
		for (int i = length - 1; i >= index; i--) {
			contents[i + count] = contents[i];
		}
		length += count;
	}

	private void contractBubble(int index, int count) {
		length -= count;
		for (int i = index; i < length; i++) {
			contents[i] = contents[i + count];
		}
	}

	public void add(int index, E element) {
		if (index < 0 || index > length) {
			// TODO: Make all IndexOutOfBoundsExceptions have more info
			throw new IndexOutOfBoundsException();
		}
		expandBubble(index, 1);
		contents[index] = element;
	}

	public E remove(int index) {
		if (index < 0 || index >= length) {
			throw new IndexOutOfBoundsException();
		}
		E out = (E) contents[index];
		contractBubble(index, 1);
		return out;
	}

	public boolean remove(Object o) {
		for (int i = 0; i < length; i++) {
			if (Objects.equals(contents[i], o)) {
				return true;
			}
		}
		return false;
	}

	public void clear() {
		length = 0;
	}

	public boolean addAll(Collection<? extends E> c) {
		if (c.isEmpty()) {
			return false;
		}
		ensureCapacity(length + c.size());
		// TODO: handle concurrent modification more gracefully
		for (E e : c) {
			contents[length++] = e;
		}
		return true;
	}

	public boolean addAll(int index, Collection<? extends E> c) {
		if (index < 0 || index > length) {
			throw new IndexOutOfBoundsException();
		}
		if (c.isEmpty()) {
			return false;
		}
		expandBubble(index, c.size());
		// TODO: handle concurrent modification more gracefully
		for (E e : c) {
			contents[index++] = e;
		}
		return true;
	}

	protected void removeRange(int fromIndex, int toIndex) {
		if (fromIndex < 0 || fromIndex >= length || toIndex > length || toIndex < fromIndex) {
			throw new IndexOutOfBoundsException();
		}
		contractBubble(fromIndex, toIndex - fromIndex);
	}

	// isEmpty, removeAll, retainAll, listIterator(0), listIterator(1) not
	// implemented because the superclass has an implementation that works just
	// fine here.

	public List<E> subList(int fromIndex, int toIndex) {
		throw new IncompleteImplementationError(); // TODO
	}
}
