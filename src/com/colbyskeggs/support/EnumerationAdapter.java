package com.colbyskeggs.support;

import java.util.Enumeration;
import java.util.Iterator;

public final class EnumerationAdapter<E> implements Enumeration<E> {

	private final Iterator<E> iter;

	public EnumerationAdapter(Iterator<E> iter) {
		this.iter = iter;
	}

	public boolean hasMoreElements() {
		return iter.hasNext();
	}

	public E nextElement() {
		return iter.next();
	}
}
