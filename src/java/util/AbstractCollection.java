package java.util;

public abstract class AbstractCollection<E> implements Collection<E> {

	protected AbstractCollection() {
	}

	public abstract Iterator<E> iterator();

	public abstract int size();

	public boolean isEmpty() {
		return size() == 0;
	}

	public boolean contains(Object o) {
		for (E e : this) {
			if (Objects.equals(e, o)) {
				return true;
			}
		}
		return false;
	}

	public Object[] toArray() {
		return new ArrayList<E>(this).toArray();
	}

	public <T> T[] toArray(T[] a) {
		return new ArrayList<E>(this).<T> toArray(a);
	}

	public boolean add(E e) {
		throw new UnsupportedOperationException("Immutable collection!");
	}

	public boolean remove(Object o) {
		for (Iterator<E> iterator = this.iterator(); iterator.hasNext();) {
			E e = iterator.next();
			if (Objects.equals(e, o)) {
				iterator.remove();
				return true;
			}
		}
		return false;
	}

	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o)) {
				return false;
			}
		}
		return true;
	}

	public boolean addAll(Collection<? extends E> c) {
		boolean changed = false;
		for (E e : c) {
			changed |= add(e);
		}
		return changed;
	}

	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for (Iterator<E> iterator = this.iterator(); iterator.hasNext();) {
			E e = iterator.next();
			if (c.contains(e)) {
				iterator.remove();
				changed = true;
			}
		}
		return changed;
	}

	public boolean retainAll(Collection<?> c) {
		boolean changed = false;
		for (Iterator<E> iterator = this.iterator(); iterator.hasNext();) {
			E e = iterator.next();
			if (!c.contains(e)) {
				iterator.remove();
				changed = true;
			}
		}
		return changed;
	}

	public void clear() {
		for (Iterator<E> iterator = this.iterator(); iterator.hasNext();) {
			iterator.next();
			iterator.remove();
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		for (E e : this) {
			sb.append(Objects.toString(e)).append(", ");
		}
		if (sb.length() > 2) {
			sb.setLength(sb.length() - 2);
		}
		sb.append("]");
		return sb.toString();
	}
}
