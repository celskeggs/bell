package java.util;

public interface List<E> extends Collection<E> {

	void add(int index, E element);

	boolean addAll(int index, Collection<? extends E> c);

	E get(int index);

	int indexOf(Object o);

	int lastIndexOf(Object o);

	ListIterator<E> listIterator();

	ListIterator<E> listIterator(int index);

	E remove(int index);

	E set(int index, E element);

	List<E> subList(int fromIndex, int toIndex);
}
