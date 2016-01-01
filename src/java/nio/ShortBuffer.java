package java.nio;

import java.io.IOException;

public abstract class ShortBuffer extends Buffer implements Comparable<ShortBuffer> {

	private final short[] array;
	private final int array_offset;

	ShortBuffer(int capacity, short[] array, int array_offset) {
		super(capacity);
		this.array = array;
		this.array_offset = array_offset;
	}

	public static ShortBuffer allocate(int capacity) {
		return wrap(new short[capacity], 0, capacity);
	}

	public static ShortBuffer wrap(short[] array, int offset, int length) {
		return new ArrayShortBuffer(array, offset, length);
	}

	public static ShortBuffer wrap(short[] array) {
		return wrap(array, 0, array.length);
	}

	public abstract ShortBuffer slice();

	public abstract ShortBuffer duplicate();

	public abstract ShortBuffer asReadOnlyBuffer();

	public abstract short get();

	public abstract ShortBuffer put(short c);

	public abstract short get(int index);

	public abstract ShortBuffer put(int index, short c);

	public ShortBuffer get(short[] dst, int offset, int length) {
		// TODO: override this in subclasses
		if (length > remaining()) {
			throw new BufferUnderflowException();
		}
		if (offset < 0 || offset > dst.length || length < 0 || length + offset > dst.length) {
			throw new IndexOutOfBoundsException();
		}
		for (int i = offset; i < offset + length; i++) {
			dst[i] = this.get();
		}
		return this;
	}

	public ShortBuffer get(short[] dst) {
		get(dst, 0, dst.length);
		return this;
	}

	public ShortBuffer put(ShortBuffer src) {
		if (src == this) {
			throw new IllegalArgumentException();
		}
		if (src.remaining() > remaining()) {
			throw new BufferOverflowException();
		}
		// TODO: override this in subclasses
		// TODO: optimize?
		while (src.hasRemaining()) {
			put(src.get());
		}
		return this;
	}

	public ShortBuffer put(short[] src, int offset, int length) {
		if (length > remaining()) {
			throw new BufferOverflowException();
		}
		if (offset < 0 || offset > src.length || length < 0 || length + offset > src.length) {
			throw new IndexOutOfBoundsException();
		}
		// TODO: override this in subclasses
		// TODO: optimize?
		for (int i = offset; i < offset + length; i++) {
			put(src[i]);
		}
		return this;
	}

	public final ShortBuffer put(short[] src) {
		this.put(src, 0, src.length);
		return this;
	}

	public final boolean hasArray() {
		return array != null && !isReadOnly();
	}

	public final short[] array() {
		if (array == null) {
			throw new UnsupportedOperationException();
		}
		if (isReadOnly()) {
			throw new ReadOnlyBufferException();
		}
		return array;
	}

	public final int arrayOffset() {
		if (array == null) {
			throw new UnsupportedOperationException();
		}
		if (isReadOnly()) {
			throw new ReadOnlyBufferException();
		}
		return array_offset;
	}

	public abstract ShortBuffer compact();

	public abstract boolean isDirect();

	public String toString() {
		String type = (isReadOnly() ? "ReadOnly" : "") + (isDirect() ? "Direct" : "") + (hasArray() ? "Backed" : "");
		return type + "ShortBuffer{" + (mark == -1 ? "no mark" : "mark = " + mark) + ", position = " + position
				+ ", limit = " + limit + ", capacity = " + capacity + "}";
	}

	public int hashCode() {
		// TODO: something better?
		int hash = 1;
		for (int i = position; i < limit; i++) {
			hash = (hash * 9) ^ ((get(i) & 0xFFFF) * (i + 1));
		}
		return hash;
	}

	public boolean equals(Object ob) {
		if (ob instanceof ShortBuffer) {
			ShortBuffer b = (ShortBuffer) ob;
			if (b.remaining() != remaining()) {
				return false;
			} else {
				int rem = remaining();
				// TODO: optimize?
				for (int i = 0; i < rem; i++) {
					if (get(i + position) != b.get(i + b.position)) {
						return false;
					}
				}
				return true;
			}
		} else {
			return false;
		}
	}

	public int compareTo(ShortBuffer that) {
		int end = Math.min(remaining(), that.remaining());
		for (int i = 0; i < end; i++) {
			int diff = this.get(this.position + i) - that.get(that.position + i);
			if (diff != 0) {
				return diff;
			}
		}
		return remaining() - that.remaining();
	}

	public abstract ByteOrder order();
}
