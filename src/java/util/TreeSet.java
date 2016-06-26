package java.util;

import com.celskeggs.support.ReverseNavigableSet;
import com.celskeggs.support.SubsetNavigableSet;

public class TreeSet<E> extends AbstractSet<E> implements NavigableSet<E>, Cloneable /* , Serializable */ {
	private static final Object existSentinel = new Object();
	private final TreeMap<E, Object> entries;
	private final NavigableSet<E> entSet;

	public TreeSet() {
		entries = new TreeMap<E, Object>();
		entSet = entries.navigableKeySet();
	}

	public TreeSet(Comparator<? super E> comparator) {
		entries = new TreeMap<E, Object>(comparator);
		entSet = entries.navigableKeySet();
	}

	public TreeSet(Collection<? extends E> c) {
		this();
		// TODO: more efficient?
		for (E e : c) {
			entries.put(e, existSentinel);
		}
	}

	public TreeSet(SortedSet<E> s) {
		this(s.comparator());
		// TODO: more efficient?
		for (E e : s) {
			entries.put(e, existSentinel);
		}
	}

	@Override
	public Iterator<E> iterator() {
		return entSet.iterator();
	}

	public Iterator<E> descendingIterator() {
		return entSet.descendingIterator();
	}

	public NavigableSet<E> descendingSet() {
		return new ReverseNavigableSet<E>(this);
	}

	@Override
	public int size() {
		return entries.size();
	}

	@Override
	public boolean isEmpty() {
		return entries.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return entries.containsKey(o);
	}

	@Override
	public boolean add(E e) {
		return entries.put(e, existSentinel) == null;
	}

	@Override
	public boolean remove(Object o) {
		return entries.remove(o) != null;
	}

	@Override
	public void clear() {
		entries.clear();
	}

	public SortedSet<E> subSet(E fromElement, E toElement) {
		return subSet(fromElement, true, toElement, false);
	}

	public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
		return new SubsetNavigableSet<E>(this, fromElement, true, fromInclusive, toElement, true, toInclusive);
	}

	public SortedSet<E> headSet(E toElement) {
		return headSet(toElement, false);
	}

	public NavigableSet<E> headSet(E toElement, boolean inclusive) {
		return new SubsetNavigableSet<E>(this, null, false, false, toElement, true, inclusive);
	}

	public SortedSet<E> tailSet(E fromElement) {
		return tailSet(fromElement, true);
	}

	public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
		return new SubsetNavigableSet<E>(this, fromElement, true, inclusive, null, false, false);
	}

	public Comparator<? super E> comparator() {
		return entries.comparator();
	}

	public E first() {
		return entries.firstKey();
	}

	public E last() {
		return entries.lastKey();
	}

	public E lower(E e) {
		return entries.lowerKey(e);
	}

	public E higher(E e) {
		return entries.higherKey(e);
	}

	public E floor(E e) {
		return entries.floorKey(e);
	}

	public E ceiling(E e) {
		return entries.ceilingKey(e);
	}

	public E pollFirst() {
		Map.Entry<E, Object> ent = entries.pollFirstEntry();
		return ent == null ? null : ent.getKey();
	}

	public E pollLast() {
		Map.Entry<E, Object> ent = entries.pollLastEntry();
		return ent == null ? null : ent.getKey();
	}
}
