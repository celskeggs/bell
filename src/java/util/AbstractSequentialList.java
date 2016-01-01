package java.util;

public abstract class AbstractSequentialList<E> extends AbstractList<E> {

	protected AbstractSequentialList() {
		super();
	}

	@Override
	public E get(int index) {
		if (index < 0 || index >= size()) {
			throw new IndexOutOfBoundsException();
		}
		return listIterator(index).next();
	}

	@Override
	public E set(int index, E element) {
		if (index < 0 || index >= size()) {
			throw new IndexOutOfBoundsException();
		}
		ListIterator<E> iterator = listIterator(index);
		E last = iterator.next();
		iterator.set(element);
		return last;
	}

	@Override
	public void add(int index, E element) {
		if (index < 0 || index > size()) {
			throw new IndexOutOfBoundsException();
		}
		listIterator(index).add(element);
	}

	@Override
	public E remove(int index) {
		if (index < 0 || index >= size()) {
			throw new IndexOutOfBoundsException();
		}
		ListIterator<E> iterator = listIterator(index);
		E last = iterator.next();
		iterator.remove();
		return last;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		if (index < 0 || index > size()) {
			throw new IndexOutOfBoundsException();
		}
		ListIterator<E> ti = listIterator(index);
		boolean change = false;
		for (E e : c) {
			change = true;
			ti.add(e);
			ti.next();
		}
		return change;
	}

	@Override
	public Iterator<E> iterator() {
		return listIterator();
	}

	public abstract ListIterator<E> listIterator(int index);
}
