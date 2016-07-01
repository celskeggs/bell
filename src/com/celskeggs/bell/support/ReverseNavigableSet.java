package com.celskeggs.bell.support;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.SortedSet;

public class ReverseNavigableSet<E> extends AbstractSet<E> implements NavigableSet<E> {

	private final NavigableSet<E> orig;

	public ReverseNavigableSet(NavigableSet<E> orig) {
		this.orig = orig;
	}

	public Comparator<? super E> comparator() {
		return Collections.reverseOrder(orig.comparator());
	}

	public E first() {
		return orig.last();
	}

	public E last() {
		return orig.first();
	}

	public boolean add(E e) {
		return orig.add(e);
	}

	public boolean addAll(Collection<? extends E> c) {
		return orig.addAll(c);
	}

	public void clear() {
		orig.clear();
	}

	public boolean contains(Object o) {
		return orig.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return orig.containsAll(c);
	}

	public boolean isEmpty() {
		return orig.isEmpty();
	}

	public boolean remove(Object o) {
		return orig.remove(o);
	}

	public boolean removeAll(Collection<?> c) {
		return orig.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return orig.retainAll(c);
	}

	public int size() {
		return orig.size();
	}

	public Object[] toArray() {
		Object[] out = orig.toArray();
		CUtil.reverseArray(out, out.length);
		return out;
	}

	public <T> T[] toArray(T[] a) {
		// Note: does not work well with concurrent modification.
		T[] t = orig.toArray(a);
		CUtil.reverseArray(t, orig.size());
		return t;
	}

	public E ceiling(E e) {
		return orig.floor(e);
	}

	public Iterator<E> descendingIterator() {
		return orig.iterator();
	}

	public NavigableSet<E> descendingSet() {
		return orig;
	}

	public E floor(E e) {
		return orig.ceiling(e);
	}

	public E higher(E e) {
		return orig.lower(e);
	}

	public Iterator<E> iterator() {
		return orig.descendingIterator();
	}

	public E lower(E e) {
		return orig.higher(e);
	}

	public E pollFirst() {
		return orig.pollLast();
	}

	public E pollLast() {
		return orig.pollFirst();
	}

	public SortedSet<E> subSet(E fromElement, E toElement) {
		return subSet(fromElement, true, toElement, false);
	}

	public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
		return orig.subSet(toElement, toInclusive, fromElement, fromInclusive).descendingSet();
	}

	public SortedSet<E> headSet(E toElement) {
		return headSet(toElement, false);
	}

	public NavigableSet<E> headSet(E toElement, boolean inclusive) {
		return orig.tailSet(toElement, inclusive).descendingSet();
	}

	public SortedSet<E> tailSet(E fromElement) {
		return tailSet(fromElement, false);
	}

	public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
		return orig.headSet(fromElement, inclusive);
	}
}
