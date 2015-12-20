package java.util;

public abstract class AbstractSet<E> extends AbstractCollection<E> implements Set<E> {
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (!(o instanceof Set)) {
			return false;
		} else {
			Set<?> s = (Set<?>) o;
			if (s.size() != size()) {
				return false;
			}
			return containsAll(s);
		}
	}

	public int hashCode() {
		int total = 0;
		for (Object o : this) {
			total += Objects.hashCode(o);
		}
		return total;
	}

	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		if (size() <= c.size()) {
			for (Iterator<E> iterator = this.iterator(); iterator.hasNext();) {
				E e = iterator.next();
				if (c.contains(e)) {
					iterator.remove();
					changed = true;
				}
			}
		} else {
			for (Object o : c) {
				changed |= remove(o);
			}
		}
		return changed;
	}
}
