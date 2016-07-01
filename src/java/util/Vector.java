package java.util;

import com.celskeggs.bell.support.CUtil;
import com.celskeggs.bell.support.EnumerationAdapter;
import com.celskeggs.bell.support.IncompleteImplementationError;

public class Vector<E> extends AbstractList<E>
		implements RandomAccess /* , Cloneable, Serializable */ {

	protected int capacityIncrement, elementCount = 0;
	protected Object[] elementData;

	// TODO: failfast iterators

	public Vector(int initialCapacity, int capacityIncrement) {
		if (initialCapacity < 0) {
			throw new IllegalArgumentException();
		}
		this.capacityIncrement = capacityIncrement;
		this.elementData = new Object[initialCapacity];
	}

	public Vector(int initialCapacity) {
		this(initialCapacity, 0);
	}

	public Vector() {
		this(10, 0);
	}

	public Vector(Collection<? extends E> c) {
		elementData = c.toArray();
		elementCount = elementData.length;
	}

	public synchronized void copyInto(Object[] anArray) {
		if (anArray.length < elementCount) {
			throw new IndexOutOfBoundsException();
		}
		for (int i = 0; i < elementCount; i++) {
			anArray[i] = elementData[i];
		}
	}

	public synchronized void trimToSize() {
		if (elementCount < elementData.length) {
			elementData = Arrays.copyOf(elementData, elementCount);
		}
	}

	public synchronized void ensureCapacity(int minCapacity) {
		if (elementData.length < minCapacity) {
			int new_size;
			if (capacityIncrement <= 0) {
				new_size = elementData.length * 2;
			} else {
				new_size = elementData.length + capacityIncrement;
			}
			new_size = Math.max(minCapacity, new_size);
			elementData = Arrays.copyOf(elementData, new_size);
		}
	}

	public synchronized void setSize(int newSize) {
		if (newSize > elementCount) {
			ensureCapacity(newSize);
			while (elementCount < newSize) {
				elementData[elementCount++] = null;
			}
		} else {
			if (newSize < 0) {
				throw new ArrayIndexOutOfBoundsException();
			}
			elementCount = newSize;
			// TODO: should I null out the old entries so that they can be GC'd?
		}
	}

	public synchronized int capacity() {
		return elementData.length;
	}

	public synchronized int size() {
		return elementCount;
	}

	public Enumeration<E> elements() {
		return new EnumerationAdapter<E>(iterator());
	}

	public synchronized boolean contains(Object o) {
		for (int i = 0; i < elementCount; i++) {
			if (Objects.equals(o, elementData[i])) {
				return true;
			}
		}
		return false;
	}

	public int indexOf(Object o) {
		return indexOf(o, 0);
	}

	public synchronized int indexOf(Object o, int index) {
		for (int i = index; i < elementCount; i++) {
			if (Objects.equals(o, elementData[i])) {
				return i;
			}
		}
		return -1;
	}

	public int lastIndexOf(Object o) {
		return lastIndexOf(o, elementCount - 1);
	}

	public synchronized int lastIndexOf(Object o, int index) {
		for (int i = index; i >= 0; i--) {
			if (Objects.equals(o, elementData[i])) {
				return i;
			}
		}
		return -1;
	}

	public synchronized E elementAt(int index) {
		if (index < 0 || index >= elementCount) {
			throw new ArrayIndexOutOfBoundsException();
		}
		return (E) elementData[index];
	}

	public synchronized E firstElement() {
		if (elementCount > 0) {
			return (E) elementData[0];
		} else {
			throw new NoSuchElementException();
		}
	}

	public synchronized E lastElement() {
		if (elementCount > 0) {
			return (E) elementData[elementCount - 1];
		} else {
			throw new NoSuchElementException();
		}
	}

	public void setElementAt(E obj, int index) {
		set(index, obj);
	}

	public void removeElementAt(int index) {
		remove(index);
	}

	public synchronized void insertElementAt(E obj, int index) {
		if (index < 0 || index > elementCount) {
			throw new ArrayIndexOutOfBoundsException();
		}
		ensureCapacity(elementCount + 1);
		for (int i = elementCount; i > index; i++) {
			elementData[i] = elementData[i - 1];
		}
		elementData[index] = obj;
		elementCount++;
	}

	public synchronized void addElement(E obj) {
		ensureCapacity(elementCount + 1);
		elementData[elementCount++] = obj;
	}

	public synchronized boolean removeElement(Object obj) {
		for (int i = 0; i < elementCount; i++) {
			if (Objects.equals(obj, elementData[i])) {
				removeElementAt(i);
				return true;
			}
		}
		return false;
	}

	public synchronized void removeAllElements() {
		elementCount = 0;
	}

	public Object clone() {
		throw new IncompleteImplementationError();
	}

	public synchronized Object[] toArray() {
		return Arrays.copyOf(elementData, elementCount);
	}

	public synchronized <T> T[] toArray(T[] a) {
		T[] n = a.length >= elementCount ? a : CUtil.copyOfType(a, elementCount);
		System.arraycopy(elementData, 0, n, 0, elementCount);
		if (a.length > elementCount) {
			n[elementCount] = null;
		}
		return n;
	}

	public E get(int index) {
		return elementAt(index);
	}

	public synchronized E set(int index, E element) {
		if (index < 0 || index >= elementCount) {
			throw new ArrayIndexOutOfBoundsException();
		}
		E old = (E) elementData[index];
		elementData[index] = element;
		return old;
	}

	public boolean add(E e) {
		addElement(e);
		return true;
	}

	public boolean remove(Object o) {
		return removeElement(o);
	}

	public void add(int index, E element) {
		insertElementAt(element, index);
	}

	public synchronized E remove(int index) {
		if (index < 0 || index >= elementCount) {
			throw new ArrayIndexOutOfBoundsException();
		}
		E out = (E) elementData[index];
		elementCount--;
		for (int i = index; i < elementCount; i++) {
			elementData[i] = elementData[i + 1];
		}
		return out;
	}

	public void clear() {
		removeAllElements();
	}

	public synchronized boolean containsAll(Collection<?> c) {
		return super.containsAll(c);
	}

	public synchronized boolean addAll(Collection<? extends E> c) {
		ensureCapacity(elementCount + c.size());
		boolean changed = false;
		// TODO: handle concurrent modification more gracefully
		for (E e : c) {
			elementData[elementCount++] = e;
			changed = true;
		}
		return changed;
	}

	public synchronized boolean addAll(int index, Collection<? extends E> c) {
		if (index < 0 || index > size()) {
			throw new ArrayIndexOutOfBoundsException();
		}
		int count = c.size();
		if (count == 0) {
			return false;
		}
		elementCount += count;
		ensureCapacity(elementCount);
		for (int i = elementCount - 1; i >= index + count; i++) {
			elementData[i] = elementData[i - count];
		}
		// TODO: handle concurrent modification more gracefully
		int i = index;
		for (E e : c) {
			elementData[i++] = e;
		}
		if (i != index + count) {
			throw new ConcurrentModificationException();
		}
		return true;
	}

	public synchronized boolean removeAll(Collection<?> c) {
		int to = 0;
		for (int from = 0; from < elementCount; from++) {
			if (c.contains(elementData[from])) {
				// do nothing: to will not advance, but from will.
			} else {
				if (to != from) {
					elementData[to] = elementData[from];
				}
				to++;
			}
		}
		if (elementCount != to) {
			elementCount = to;
			return true;
		}
		return false;
	}

	public synchronized boolean retainAll(Collection<?> c) {
		int to = 0;
		for (int from = 0; from < elementCount; from++) {
			if (!c.contains(elementData[from])) {
				// do nothing: to will not advance, but from will.
			} else {
				if (to != from) {
					elementData[to] = elementData[from];
				}
				to++;
			}
		}
		if (elementCount != to) {
			elementCount = to;
			return true;
		}
		return false;
	}

	protected synchronized void removeRange(int fromIndex, int toIndex) {
		int count = toIndex - fromIndex;
		if (count > 0) {
			elementCount -= count;
			for (int i = fromIndex; i < elementCount; i++) {
				elementData[i] = elementData[i + count];
			}
		}
	}
}
