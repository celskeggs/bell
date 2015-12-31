package java.nio;

import java.io.IOException;

public abstract class IntBuffer extends Buffer implements Comparable<IntBuffer> {

	private final int[] array;
	private final int array_offset;

	IntBuffer(int capacity, int[] array, int array_offset) {
		super(capacity);
		this.array = array;
		this.array_offset = array_offset;
	}

	public static IntBuffer allocate(int capacity) {
		return wrap(new int[capacity], 0, capacity);
	}

	public static IntBuffer wrap(int[] array, int offset, int length) {
		return new ArrayIntBuffer(array, offset, length);
	}

	public static IntBuffer wrap(int[] array) {
		return wrap(array, 0, array.length);
	}

	public abstract IntBuffer slice();

	public abstract IntBuffer duplicate();

	public abstract IntBuffer asReadOnlyBuffer();

	public abstract int get();

	public abstract IntBuffer put(int i);

	public abstract int get(int index);

	public abstract IntBuffer put(int index, int i);

	public IntBuffer get(int[] dst, int offset, int length) {
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

	public IntBuffer get(int[] dst) {
		get(dst, 0, dst.length);
		return this;
	}

	public IntBuffer put(IntBuffer src) {
		if (src == this) {
			throw new IllegalArgumentException();
		}
		// TODO: override this in subclasses
		// TODO: optimize?
		while (src.hasRemaining()) {
			put(src.get());
		}
		return this;
	}

	public IntBuffer put(int[] src, int offset, int length) {
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

	public final IntBuffer put(int[] src) {
		this.put(src, 0, src.length);
		return this;
	}

	public final boolean hasArray() {
		return array != null && !isReadOnly();
	}

	public final int[] array() {
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

	public abstract IntBuffer compact();

	public abstract boolean isDirect();

	public String toString() {
		String type = (isReadOnly() ? "ReadOnly" : "") + (isDirect() ? "Direct" : "") + (hasArray() ? "Backed" : "");
		return type + "IntBuffer{" + (mark == -1 ? "no mark" : "mark = " + mark) + ", position = " + position
				+ ", limit = " + limit + ", capacity = " + capacity + "}";
	}

	public int hashCode() {
		// TODO: something better?
		int hash = 1;
		for (int i = position; i < limit; i++) {
			hash = (hash * 9) ^ (get(i) * (i + 1));
		}
		return hash;
	}

	public boolean equals(Object ob) {
		if (ob instanceof IntBuffer) {
			IntBuffer b = (IntBuffer) ob;
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

	public int compareTo(IntBuffer that) {
		int end = Math.min(remaining(), that.remaining());
		for (int i = 0; i < end; i++) {
			int diff = Integer.compare(this.get(this.position + i), that.get(that.position + i));
			if (diff != 0) {
				return diff;
			}
		}
		return remaining() - that.remaining();
	}

	public abstract ByteOrder order();
}
