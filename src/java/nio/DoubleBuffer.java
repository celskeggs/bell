package java.nio;

public abstract class DoubleBuffer extends Buffer implements Comparable<DoubleBuffer> {

	final double[] array;
	final int array_offset;

	DoubleBuffer(int capacity, double[] array, int array_offset) {
		super(capacity);
		this.array = array;
		this.array_offset = array_offset;
	}

	public static DoubleBuffer allocate(int capacity) {
		return wrap(new double[capacity], 0, capacity);
	}

	public static DoubleBuffer wrap(double[] array, int offset, int length) {
		if (offset < 0 || offset > array.length || length < 0 || length > array.length - offset) {
			throw new IndexOutOfBoundsException();
		}
		return new ArrayDoubleBuffer(array, offset, length);
	}

	public static DoubleBuffer wrap(double[] array) {
		return wrap(array, 0, array.length);
	}

	public abstract DoubleBuffer slice();

	public abstract DoubleBuffer duplicate();

	public abstract DoubleBuffer asReadOnlyBuffer();

	public abstract double get();

	public abstract DoubleBuffer put(double c);

	public abstract double get(int index);

	public abstract DoubleBuffer put(int index, double c);

	public DoubleBuffer get(double[] dst, int offset, int length) {
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

	public DoubleBuffer get(double[] dst) {
		get(dst, 0, dst.length);
		return this;
	}

	public DoubleBuffer put(DoubleBuffer src) {
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

	public DoubleBuffer put(double[] src, int offset, int length) {
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

	public final DoubleBuffer put(double[] src) {
		this.put(src, 0, src.length);
		return this;
	}

	public final boolean hasArray() {
		return array != null && !isReadOnly();
	}

	public final double[] array() {
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

	public abstract DoubleBuffer compact();

	public abstract boolean isDirect();

	public String toString() {
		String type = (isReadOnly() ? "ReadOnly" : "") + (isDirect() ? "Direct" : "") + (hasArray() ? "Backed" : "");
		return type + "DoubleBuffer{" + (mark == -1 ? "no mark" : "mark = " + mark) + ", position = " + position
				+ ", limit = " + limit + ", capacity = " + capacity + "}";
	}

	public int hashCode() {
		// TODO: something better?
		long hash = 1;
		for (int i = position; i < limit; i++) {
			hash = (hash * 9) ^ (Double.doubleToLongBits(get(i)) * (i + 1));
		}
		return ((int) (hash >> 32)) ^ (int) hash;
	}

	public boolean equals(Object ob) {
		if (ob instanceof DoubleBuffer) {
			DoubleBuffer buf = (DoubleBuffer) ob;
			if (buf.remaining() != remaining()) {
				return false;
			} else {
				int rem = remaining();
				// TODO: optimize?
				for (int i = 0; i < rem; i++) {
					double a = get(i + position), b = get(i + buf.position);
					if (a != b && !(Double.isNaN(a) && Double.isNaN(b))) {
						return false;
					}
				}
				return true;
			}
		} else {
			return false;
		}
	}

	private static int compare(double x, double y) {
		if (Double.isNaN(x)) {
			if (Double.isNaN(y)) {
				return 0;
			} else {
				return 1;
			}
		} else if (Double.isNaN(y)) {
			return -1;
		} else {
			if (x < y) {
				return -1;
			} else if (x > y) {
				return 1;
			} else {
				// a slight difference from Double.compare
				return 0;
			}
		}
	}

	public int compareTo(DoubleBuffer that) {
		int end = Math.min(remaining(), that.remaining());
		for (int i = 0; i < end; i++) {
			int diff = compare(this.get(this.position + i), that.get(that.position + i));
			if (diff != 0) {
				return diff;
			}
		}
		return remaining() - that.remaining();
	}

	public abstract ByteOrder order();
}
