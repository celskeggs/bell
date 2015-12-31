package java.nio;

import java.io.IOException;

public abstract class FloatBuffer extends Buffer implements Comparable<FloatBuffer> {

	private final float[] array;
	private final int array_offset;

	FloatBuffer(int capacity, float[] array, int array_offset) {
		super(capacity);
		this.array = array;
		this.array_offset = array_offset;
	}

	public static FloatBuffer allocate(int capacity) {
		return wrap(new float[capacity], 0, capacity);
	}

	public static FloatBuffer wrap(float[] array, int offset, int length) {
		return new ArrayFloatBuffer(array, offset, length);
	}

	public static FloatBuffer wrap(float[] array) {
		return wrap(array, 0, array.length);
	}

	public abstract FloatBuffer slice();

	public abstract FloatBuffer duplicate();

	public abstract FloatBuffer asReadOnlyBuffer();

	public abstract float get();

	public abstract FloatBuffer put(float c);

	public abstract float get(int index);

	public abstract FloatBuffer put(int index, float c);

	public FloatBuffer get(float[] dst, int offset, int length) {
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

	public FloatBuffer get(float[] dst) {
		get(dst, 0, dst.length);
		return this;
	}

	public FloatBuffer put(FloatBuffer src) {
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

	public FloatBuffer put(float[] src, int offset, int length) {
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

	public final FloatBuffer put(float[] src) {
		this.put(src, 0, src.length);
		return this;
	}

	public final boolean hasArray() {
		return array != null && !isReadOnly();
	}

	public final float[] array() {
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

	public abstract FloatBuffer compact();

	public abstract boolean isDirect();

	public String toString() {
		String type = (isReadOnly() ? "ReadOnly" : "") + (isDirect() ? "Direct" : "") + (hasArray() ? "Backed" : "");
		return type + "FloatBuffer{" + (mark == -1 ? "no mark" : "mark = " + mark) + ", position = " + position
				+ ", limit = " + limit + ", capacity = " + capacity + "}";
	}

	public int hashCode() {
		// TODO: something better?
		int hash = 1;
		for (int i = position; i < limit; i++) {
			hash = (hash * 9) ^ (Float.floatToIntBits(get(i)) * (i + 1));
		}
		return hash;
	}

	public boolean equals(Object ob) {
		if (ob instanceof FloatBuffer) {
			FloatBuffer buf = (FloatBuffer) ob;
			if (buf.remaining() != remaining()) {
				return false;
			} else {
				int rem = remaining();
				// TODO: optimize?
				for (int i = 0; i < rem; i++) {
					float a = get(i + position), b = get(i + buf.position);
					if (a != b && !(Float.isNaN(a) && Float.isNaN(b))) {
						return false;
					}
				}
				return true;
			}
		} else {
			return false;
		}
	}

	private static int compare(float x, float y) {
		if (Float.isNaN(x)) {
			if (Float.isNaN(y)) {
				return 0;
			} else {
				return 1;
			}
		} else if (Float.isNaN(y)) {
			return -1;
		} else {
			if (x < y) {
				return -1;
			} else if (x > y) {
				return 1;
			} else {
				// a slight difference from Float.compare
				return 0;
			}
		}
	}

	public int compareTo(FloatBuffer that) {
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
