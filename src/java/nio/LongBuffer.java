package java.nio;

public abstract class LongBuffer extends Buffer implements Comparable<LongBuffer> {

	final long[] array;
	final int array_offset;

	LongBuffer(int capacity, long[] array, int array_offset) {
		super(capacity);
		this.array = array;
		this.array_offset = array_offset;
	}

	public static LongBuffer allocate(int capacity) {
		return wrap(new long[capacity], 0, capacity);
	}

	public static LongBuffer wrap(long[] array, int offset, int length) {
		if (offset < 0 || offset > array.length || length < 0 || length > array.length - offset) {
			throw new IndexOutOfBoundsException();
		}
		return new ArrayLongBuffer(array, offset, length);
	}

	public static LongBuffer wrap(long[] array) {
		return wrap(array, 0, array.length);
	}

	public abstract LongBuffer slice();

	public abstract LongBuffer duplicate();

	public abstract LongBuffer asReadOnlyBuffer();

	public abstract long get();

	public abstract LongBuffer put(long c);

	public abstract long get(int index);

	public abstract LongBuffer put(int index, long c);

	public LongBuffer get(long[] dst, int offset, int length) {
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

	public LongBuffer get(long[] dst) {
		get(dst, 0, dst.length);
		return this;
	}

	public LongBuffer put(LongBuffer src) {
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

	public LongBuffer put(long[] src, int offset, int length) {
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

	public final LongBuffer put(long[] src) {
		this.put(src, 0, src.length);
		return this;
	}

	public final boolean hasArray() {
		return array != null && !isReadOnly();
	}

	public final long[] array() {
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

	public abstract LongBuffer compact();

	public abstract boolean isDirect();

	public String toString() {
		String type = (isReadOnly() ? "ReadOnly" : "") + (isDirect() ? "Direct" : "") + (hasArray() ? "Backed" : "");
		return type + "LongBuffer{" + (mark == -1 ? "no mark" : "mark = " + mark) + ", position = " + position
				+ ", limit = " + limit + ", capacity = " + capacity + "}";
	}

	public int hashCode() {
		// TODO: something better?
		long hash = 1;
		for (int i = position; i < limit; i++) {
			hash = (hash * 9) ^ (get(i) * (i + 1));
		}
		return ((int) (hash >> 32)) ^ (int) hash;
	}

	public boolean equals(Object ob) {
		if (ob instanceof LongBuffer) {
			LongBuffer b = (LongBuffer) ob;
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

	public int compareTo(LongBuffer that) {
		int end = Math.min(remaining(), that.remaining());
		for (int i = 0; i < end; i++) {
			int diff = Long.compare(this.get(this.position + i), that.get(that.position + i));
			if (diff != 0) {
				return diff;
			}
		}
		return remaining() - that.remaining();
	}

	public abstract ByteOrder order();
}
