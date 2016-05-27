package java.util;

public interface SortedSet<E> extends Set<E> {
	Comparator<? super E> comparator();

	E first();

	SortedSet<E> headSet(E toElement);

	E last();

	SortedSet<E> subSet(E fromElement, E toElement);

	SortedSet<E> tailSet(E fromElement);
}
