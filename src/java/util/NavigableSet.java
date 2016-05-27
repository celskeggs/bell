package java.util;

public interface NavigableSet<E> extends SortedSet<E> {
	E ceiling(E e);

	Iterator<E> descendingIterator();

	NavigableSet<E> descendingSet();

	E floor(E e);

	SortedSet<E> headSet(E toElement);

	NavigableSet<E> headSet(E toElement, boolean inclusive);

	E higher(E e);

	Iterator<E> iterator();

	E lower(E e);

	E pollFirst();

	E pollLast();

	NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive);

	SortedSet<E> subSet(E fromElement, E toElement);

	SortedSet<E> tailSet(E fromElement);

	NavigableSet<E> tailSet(E fromElement, boolean inclusive);
}
