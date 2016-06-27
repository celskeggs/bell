package java.nio;

import com.celskeggs.support.IncompleteImplementationError;

public abstract class ByteBuffer extends Buffer implements Comparable<ByteBuffer> {

	final byte[] array;
	final int array_offset;
	ByteOrder order;

	ByteBuffer(int capacity, byte[] array, int array_offset) {
		super(capacity);
		this.array = array;
		this.array_offset = array_offset;
	}

	public static ByteBuffer allocateDirect(int capacity) {
		throw new IncompleteImplementationError();
	}

	public static ByteBuffer allocate(int capacity) {
		return wrap(new byte[capacity], 0, capacity);
	}

	public static ByteBuffer wrap(byte[] array, int offset, int length) {
		if (offset < 0 || offset > array.length || length < 0 || length > array.length - offset) {
			throw new IndexOutOfBoundsException();
		}
		return new ArrayByteBuffer(array, offset, length);
	}

	public static ByteBuffer wrap(byte[] array) {
		return wrap(array, 0, array.length);
	}

	public abstract ByteBuffer slice();

	public abstract ByteBuffer duplicate();

	public abstract ByteBuffer asReadOnlyBuffer();

	public abstract byte get();

	public abstract ByteBuffer put(byte b);

	public abstract byte get(int index);

	public abstract ByteBuffer put(int index, byte b);

	public ByteBuffer get(byte[] dst, int offset, int length) {
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

	public ByteBuffer get(byte[] dst) {
		get(dst, 0, dst.length);
		return this;
	}

	public ByteBuffer put(ByteBuffer src) {
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

	public ByteBuffer put(byte[] src, int offset, int length) {
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

	public final ByteBuffer put(byte[] src) {
		this.put(src, 0, src.length);
		return this;
	}

	public final boolean hasArray() {
		return array != null && !isReadOnly();
	}

	public final byte[] array() {
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

	public abstract ByteBuffer compact();

	public abstract boolean isDirect();

	public String toString() {
		String type = (isReadOnly() ? "ReadOnly" : "") + (isDirect() ? "Direct" : "") + (hasArray() ? "Backed" : "");
		return type + "ByteBuffer{" + (mark == -1 ? "no mark" : "mark = " + mark) + ", position = " + position
				+ ", limit = " + limit + ", capacity = " + capacity + "}";
	}

	public int hashCode() {
		// TODO: something better?
		int hash = 1;
		for (int i = position; i < limit; i++) {
			hash = (hash * 9) ^ ((get(i) & 0xFF) * (i + 1));
		}
		return hash;
	}

	public boolean equals(Object ob) {
		if (ob instanceof ByteBuffer) {
			ByteBuffer b = (ByteBuffer) ob;
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

	public int compareTo(ByteBuffer that) {
		// TODO: optimize?
		int end = Math.min(remaining(), that.remaining());
		for (int i = 0; i < end; i++) {
			int diff = this.get(this.position + i) - that.get(that.position + i);
			if (diff != 0) {
				return diff;
			}
		}
		return remaining() - that.remaining();
	}

	public final ByteOrder order() {
		return this.order;
	}

	public final ByteBuffer order(ByteOrder bo) {
		this.order = bo;
		return this;
	}

	public abstract char getChar();

	public abstract ByteBuffer putChar(char value);

	public abstract char getChar(int index);

	public abstract ByteBuffer putChar(int index, char value);

	public abstract CharBuffer asCharBuffer();

	public abstract short getShort();

	public abstract ByteBuffer putShort(short value);

	public abstract short getShort(int index);

	public abstract ByteBuffer putShort(int index, short value);

	public abstract ShortBuffer asShortBuffer();

	public abstract int getInt();

	public abstract ByteBuffer putInt(int value);

	public abstract int getInt(int index);

	public abstract ByteBuffer putInt(int index, int value);

	public abstract IntBuffer asIntBuffer();

	public abstract long getLong();

	public abstract ByteBuffer putLong(long value);

	public abstract long getLong(int index);

	public abstract ByteBuffer putLong(int index, long value);

	public abstract LongBuffer asLongBuffer();

	public abstract float getFloat();

	public abstract ByteBuffer putFloat(float value);

	public abstract float getFloat(int index);

	public abstract ByteBuffer putFloat(int index, float value);

	public abstract FloatBuffer asFloatBuffer();

	public abstract double getDouble();

	public abstract ByteBuffer putDouble(double value);

	public abstract double getDouble(int index);

	public abstract ByteBuffer putDouble(int index, double value);

	public abstract DoubleBuffer asDoubleBuffer();
}
