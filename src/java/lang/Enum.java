package java.lang;

import com.celskeggs.bell.support.IncompleteImplementationError;

public abstract class Enum<E extends Enum<E>>
		implements Comparable<E> /* , Serializable */ {

	private final int ordinal;
	private final String name;

	protected Enum(String name, int ordinal) {
		this.name = name;
		this.ordinal = ordinal;
	}

	public final String name() {
		return name;
	}

	public final int ordinal() {
		return ordinal;
	}

	public String toString() {
		return name;
	}

	public final boolean equals(Object other) {
		return this == other;
	}

	public final int hashCode() {
		return ordinal;
	}

	protected final Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Enums cannot be cloned.");
	}

	public int compareTo(E o) {
	    Enum<E> oe = o;
		return ordinal - oe.ordinal;
	}

	public final Class<E> getDeclaringClass() {
		throw new IncompleteImplementationError();
	}

	public static <T extends Enum<T>> T valueOf(Class<T> enumType, String name) {
		throw new IncompleteImplementationError();
	}

	protected final void finalize() {
		// enums cannot have finalize methods
	}
}
