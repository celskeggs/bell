package com.celskeggs.support;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.SortedSet;

public class SubsetNavigableSet<E> extends AbstractSet<E> implements NavigableSet<E> {

	private final NavigableSet<E> orig;
	private final E fromElement;
	private final boolean fromExists;
	private final boolean fromInclusive;
	private final E toElement;
	private final boolean toExists;
	private final boolean toInclusive;

	public SubsetNavigableSet(NavigableSet<E> orig, E fromElement, boolean fromExists, boolean fromInclusive,
			E toElement, boolean toExists, boolean toInclusive) {
		this.orig = orig;
		this.fromElement = fromElement;
		this.fromExists = fromExists;
		this.fromInclusive = fromInclusive;
		this.toElement = toElement;
		this.toExists = toExists;
		this.toInclusive = toInclusive;
	}

	public Comparator<? super E> comparator() {
		return orig.comparator();
	}

	public E first() {
		if (fromExists) {
			if (isEmpty()) {
				throw new NoSuchElementException();
			}
			return fromInclusive ? orig.ceiling(fromElement) : orig.higher(fromElement);
		} else {
			return orig.first();
		}
	}

	public E last() {
		if (toExists) {
			if (isEmpty()) {
				throw new NoSuchElementException();
			}
			return toInclusive ? orig.floor(toElement) : orig.lower(toElement);
		} else {
			return orig.last();
		}
	}

	private boolean rangeIncludes(E e) {
		if (fromExists) {
			int cmp = orig.comparator().compare(e, fromElement);
			if (cmp < 0 || (cmp == 0 && !fromInclusive)) {
				return false;
			}
		}
		if (toExists) {
			int cmp = orig.comparator().compare(e, toElement);
			if (cmp > 0 || (cmp == 0 && !toInclusive)) {
				return false;
			}
		}
		return true;
	}

	public boolean add(E e) {
		if (rangeIncludes(e)) {
			return orig.add(e);
		} else {
			throw new IllegalArgumentException("Attempt to insert outside of subset range!");
		}
	}

	public boolean contains(Object o) {
		return rangeIncludes((E) o) && orig.contains(o);
	}

	public boolean isEmpty() {
		return orig.isEmpty() || !this.iterator().hasNext();
	}

	public boolean remove(Object o) {
		return rangeIncludes((E) o) && orig.remove(o);
	}

	public int size() {
		int count = 0;
		for (E _ : this) {
			count++;
		}
		return count;
	}

	public E ceiling(E e) {
		E out;
		if (fromExists && orig.comparator().compare(e, fromElement) <= 0) {
			if (fromInclusive) {
				out = orig.ceiling(fromElement);
			} else {
				out = orig.higher(fromElement);
			}
		} else {
			out = orig.ceiling(e);
		}
		return out != null && rangeIncludes(out) ? out : null;
	}

	public E floor(E e) {
		E out;
		if (toExists && orig.comparator().compare(e, toElement) >= 0) {
			if (toInclusive) {
				out = orig.floor(toElement);
			} else {
				out = orig.lower(toElement);
			}
		} else {
			out = orig.floor(e);
		}
		return out != null && rangeIncludes(out) ? out : null;
	}

	public E higher(E e) {
		E out;
		if (fromExists && orig.comparator().compare(e, fromElement) < 0) {
			if (fromInclusive) {
				out = orig.ceiling(fromElement);
			} else {
				out = orig.higher(fromElement);
			}
		} else {
			out = orig.higher(e);
		}
		return out != null && rangeIncludes(out) ? out : null;
	}

	public E lower(E e) {
		E out;
		if (toExists && orig.comparator().compare(e, toElement) > 0) {
			if (toInclusive) {
				out = orig.floor(toElement);
			} else {
				out = orig.lower(toElement);
			}
		} else {
			out = orig.lower(e);
		}
		return out != null && rangeIncludes(out) ? out : null;
	}

	public SortedSet<E> headSet(E toElement) {
		return headSet(toElement, false);
	}

	public SortedSet<E> tailSet(E fromElement) {
		return tailSet(fromElement, true);
	}

	public SortedSet<E> subSet(E fromElement, E toElement) {
		return subSet(fromElement, true, toElement, false);
	}

	public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
		if (fromExists) {
			int cmp = orig.comparator().compare(fromElement, this.fromElement);
			if (cmp < 0) {
				fromElement = this.fromElement;
				fromInclusive = this.fromInclusive;
			} else if (cmp == 0) {
				fromInclusive &= this.fromInclusive;
			}
		}
		if (toExists) {
			int cmp = orig.comparator().compare(toElement, this.toElement);
			if (cmp > 0) {
				toElement = this.toElement;
				toInclusive = this.toInclusive;
			} else if (cmp == 0) {
				toInclusive &= this.toInclusive;
			}
		}
		return new SubsetNavigableSet<E>(orig, fromElement, true, fromInclusive, toElement, true, toInclusive);
	}

	public NavigableSet<E> headSet(E toElement, boolean toInclusive) {
		if (toExists) {
			int cmp = orig.comparator().compare(toElement, this.toElement);
			if (cmp > 0) {
				toElement = this.toElement;
				toInclusive = this.toInclusive;
			} else if (cmp == 0) {
				toInclusive &= this.toInclusive;
			}
		}
		return new SubsetNavigableSet<E>(orig, fromElement, fromExists, fromInclusive, toElement, true, toInclusive);
	}

	public NavigableSet<E> tailSet(E fromElement, boolean fromInclusive) {
		if (fromExists) {
			int cmp = orig.comparator().compare(fromElement, this.fromElement);
			if (cmp < 0) {
				fromElement = this.fromElement;
				fromInclusive = this.fromInclusive;
			} else if (cmp == 0) {
				fromInclusive &= this.fromInclusive;
			}
		}
		return new SubsetNavigableSet<E>(orig, fromElement, true, fromInclusive, toElement, toExists, toInclusive);
	}

	public Iterator<E> iterator() {
		return iteratorDir(false);
	}

	private Iterator<E> iteratorDir(final boolean reverse) {
		if (isEmpty()) {
			return Collections.emptyIterator();
		}
		// TODO: more efficient?
		return new Iterator<E>() {
			private E element = reverse ? last() : first();
			private final E last = reverse ? first() : last();
			private boolean exists = true;
			private E prev;
			private boolean prevExists = false;

			public boolean hasNext() {
				return exists;
			}

			public E next() {
				if (!exists) {
					throw new NoSuchElementException();
				}
				prevExists = true;
				prev = element;
				E out = reverse ? lower(element) : higher(element);
				if (out == null && element == last) {
					// not an actual 'null'!
					exists = false;
				}
				element = out;
				return prev;
			}

			public void remove() {
				if (!prevExists) {
					throw new IllegalStateException();
				}
				prevExists = false;
				orig.remove(prev);
			}
		};
	}

	public Iterator<E> descendingIterator() {
		return iteratorDir(true);
	}

	public NavigableSet<E> descendingSet() {
		return new ReverseNavigableSet<E>(this);
	}

	public E pollFirst() {
		if (fromExists) {
			if (isEmpty()) {
				return null;
			}
			E e = fromInclusive ? orig.ceiling(fromElement) : orig.higher(fromElement);
			orig.remove(e);
			return e;
		} else {
			return orig.pollFirst();
		}
	}

	public E pollLast() {
		if (toExists) {
			if (isEmpty()) {
				return null;
			}
			E e = toInclusive ? orig.floor(toElement) : orig.lower(toElement);
			orig.remove(e);
			return e;
		} else {
			return orig.pollLast();
		}
	}
}
