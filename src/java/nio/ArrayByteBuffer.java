package java.nio;

final class ArrayByteBuffer extends ByteBuffer {

	private final boolean read_only;

	public ArrayByteBuffer(byte[] array, int offset, int length) {
		super(array.length, array, 0);
		position = offset;
		limit = offset + length;
		read_only = false;
	}

	private ArrayByteBuffer(byte[] array, int array_offset, int capacity, int position, int limit, boolean read_only) {
		super(capacity, array, array_offset);
		this.position = position;
		this.limit = limit;
		this.read_only = read_only;
	}

	@Override
	public ByteBuffer slice() {
		return new ArrayByteBuffer(array, position(), remaining(), 0, remaining(), read_only);
	}

	@Override
	public ByteBuffer duplicate() {
		ByteBuffer b = new ArrayByteBuffer(array, array_offset, capacity, position, limit, read_only);
		b.mark = mark;
		return b;
	}

	@Override
	public ByteBuffer asReadOnlyBuffer() {
		ByteBuffer b = new ArrayByteBuffer(array, array_offset, capacity, position, limit, true);
		b.mark = mark;
		return b;
	}

	@Override
	public byte get() {
		if (position < limit) {
			return array[array_offset + position++];
		}
		throw new BufferUnderflowException();
	}

	private void checkReadOnly() {
		if (read_only) {
			throw new ReadOnlyBufferException();
		}
	}

	@Override
	public ByteBuffer put(byte b) {
		checkReadOnly();
		if (position < limit) {
			array[array_offset + position++] = b;
			return this;
		}
		throw new BufferOverflowException();
	}

	@Override
	public byte get(int index) {
		if (index < 0 || index >= limit) {
			throw new IndexOutOfBoundsException();
		}
		return array[array_offset + index];
	}

	@Override
	public ByteBuffer put(int index, byte b) {
		checkReadOnly();
		if (index < 0 || index >= limit) {
			throw new IndexOutOfBoundsException();
		}
		array[array_offset + index] = b;
		return this;
	}

	public ByteBuffer get(byte[] dst, int offset, int length) {
		if (length > remaining()) {
			throw new BufferUnderflowException();
		}
		if (offset < 0 || offset > dst.length || length < 0 || length + offset > dst.length) {
			throw new IndexOutOfBoundsException();
		}
		System.arraycopy(array, array_offset + position, dst, offset, length);
		position += length;
		return this;
	}

	public ByteBuffer get(byte[] dst) {
		if (dst.length > remaining()) {
			throw new BufferUnderflowException();
		}
		System.arraycopy(array, array_offset + position, dst, 0, dst.length);
		position += dst.length;
		return this;
	}

	public ByteBuffer put(ByteBuffer src) {
		checkReadOnly();
		if (src == this) {
			throw new IllegalArgumentException();
		}
		if (src.remaining() > remaining()) {
			throw new BufferOverflowException();
		}
		int count = src.remaining();
		src.get(array, array_offset + position, count);
		position += count;
		return this;
	}

	public ByteBuffer put(byte[] src, int offset, int length) {
		checkReadOnly();
		if (length > remaining()) {
			throw new BufferOverflowException();
		}
		if (offset < 0 || offset > src.length || length < 0 || length + offset > src.length) {
			throw new IndexOutOfBoundsException();
		}
		System.arraycopy(src, offset, array, array_offset + position, length);
		position += length;
		return this;
	}

	@Override
	public ByteBuffer compact() {
		checkReadOnly();
		System.arraycopy(array, array_offset + position, array, array_offset, limit - position);
		position = limit - position;
		limit = capacity;
		mark = -1;
		return this;
	}

	@Override
	public boolean isDirect() {
		return false;
	}

	@Override
	public char getChar() {
		if (position + 1 >= limit) {
			throw new BufferUnderflowException();
		}
		char out = getChar(position);
		position += 2;
		return out;
	}

	@Override
	public ByteBuffer putChar(char value) {
		if (position + 1 >= limit) {
			throw new BufferOverflowException();
		}
		putChar(position, value);
		position += 2;
		return this;
	}

	@Override
	public char getChar(int index) {
		if (index < 0 || index + 1 >= limit) {
			throw new IndexOutOfBoundsException();
		}
		int f = array[array_offset + index] & 0xFF;
		int s = array[array_offset + index + 1] & 0xFF;
		if (order == ByteOrder.LITTLE_ENDIAN) {
			return (char) (f | (s << 8));
		} else {
			return (char) ((f << 8) | s);
		}
	}

	@Override
	public ByteBuffer putChar(int index, char value) {
		checkReadOnly();
		if (index < 0 || index + 1 >= limit) {
			throw new IndexOutOfBoundsException();
		}
		if (order == ByteOrder.LITTLE_ENDIAN) {
			array[array_offset + index] = (byte) value;
			array[array_offset + index + 1] = (byte) (value >> 8);
		} else {
			array[array_offset + index] = (byte) (value >> 8);
			array[array_offset + index + 1] = (byte) value;
		}
		return this;
	}

	@Override
	public short getShort() {
		// both are 16 bits
		return (short) getChar();
	}

	@Override
	public ByteBuffer putShort(short value) {
		putChar((char) value);
		return this;
	}

	@Override
	public short getShort(int index) {
		return (short) getChar(index);
	}

	@Override
	public ByteBuffer putShort(int index, short value) {
		putChar(index, (char) value);
		return this;
	}

	@Override
	public int getInt() {
		if (remaining() < 4) {
			throw new BufferUnderflowException();
		}
		int out = getInt(position);
		position += 4;
		return out;
	}

	@Override
	public ByteBuffer putInt(int value) {
		if (remaining() < 4) {
			throw new BufferOverflowException();
		}
		putInt(position, value);
		position += 4;
		return this;
	}

	@Override
	public int getInt(int index) {
		if (index < 0 || index + 3 >= limit) {
			throw new IndexOutOfBoundsException();
		}
		int b1 = array[array_offset + index] & 0xFF;
		int b2 = array[array_offset + index + 1] & 0xFF;
		int b3 = array[array_offset + index + 2] & 0xFF;
		int b4 = array[array_offset + index + 3] & 0xFF;
		if (order == ByteOrder.LITTLE_ENDIAN) {
			return b1 | (b2 << 8) | (b3 << 16) | (b4 << 24);
		} else {
			return (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
		}
	}

	@Override
	public ByteBuffer putInt(int index, int value) {
		checkReadOnly();
		if (index < 0 || index + 3 >= limit) {
			throw new IndexOutOfBoundsException();
		}
		if (order == ByteOrder.LITTLE_ENDIAN) {
			array[array_offset + index] = (byte) value;
			array[array_offset + index + 1] = (byte) (value >> 8);
			array[array_offset + index + 2] = (byte) (value >> 16);
			array[array_offset + index + 3] = (byte) (value >> 24);
		} else {
			array[array_offset + index] = (byte) (value >> 24);
			array[array_offset + index + 1] = (byte) (value >> 16);
			array[array_offset + index + 2] = (byte) (value >> 8);
			array[array_offset + index + 3] = (byte) value;
		}
		return this;
	}

	@Override
	public long getLong() {
		if (remaining() < 8) {
			throw new BufferUnderflowException();
		}
		long out = getLong(position);
		position += 8;
		return out;
	}

	@Override
	public ByteBuffer putLong(long value) {
		if (remaining() < 8) {
			throw new BufferOverflowException();
		}
		putLong(position, value);
		position += 8;
		return this;
	}

	@Override
	public long getLong(int index) {
		if (index < 0 || index + 7 >= limit) {
			throw new IndexOutOfBoundsException();
		}
		long b1 = array[array_offset + index] & 0xFF;
		long b2 = array[array_offset + index + 1] & 0xFF;
		long b3 = array[array_offset + index + 2] & 0xFF;
		long b4 = array[array_offset + index + 3] & 0xFF;
		long b5 = array[array_offset + index] & 0xFF;
		long b6 = array[array_offset + index + 1] & 0xFF;
		long b7 = array[array_offset + index + 2] & 0xFF;
		long b8 = array[array_offset + index + 3] & 0xFF;
		if (order == ByteOrder.LITTLE_ENDIAN) {
			return b1 | (b2 << 8) | (b3 << 16) | (b4 << 24) | (b5 << 32) | (b6 << 40) | (b7 << 48) | (b8 << 56);
		} else {
			return (b1 << 56) | (b2 << 48) | (b3 << 40) | (b4 << 32) | (b5 << 24) | (b6 << 16) | (b7 << 8) | b8;
		}
	}

	@Override
	public ByteBuffer putLong(int index, long value) {
		checkReadOnly();
		if (index < 0 || index + 7 >= limit) {
			throw new IndexOutOfBoundsException();
		}
		if (order == ByteOrder.LITTLE_ENDIAN) {
			array[array_offset + index] = (byte) value;
			array[array_offset + index + 1] = (byte) (value >> 8);
			array[array_offset + index + 2] = (byte) (value >> 16);
			array[array_offset + index + 3] = (byte) (value >> 24);
			array[array_offset + index + 4] = (byte) (value >> 32);
			array[array_offset + index + 5] = (byte) (value >> 40);
			array[array_offset + index + 6] = (byte) (value >> 48);
			array[array_offset + index + 7] = (byte) (value >> 56);
		} else {
			array[array_offset + index] = (byte) (value >> 56);
			array[array_offset + index + 1] = (byte) (value >> 48);
			array[array_offset + index + 2] = (byte) (value >> 40);
			array[array_offset + index + 3] = (byte) (value >> 32);
			array[array_offset + index + 4] = (byte) (value >> 24);
			array[array_offset + index + 5] = (byte) (value >> 16);
			array[array_offset + index + 6] = (byte) (value >> 8);
			array[array_offset + index + 7] = (byte) value;
		}
		return this;
	}

	@Override
	public float getFloat() {
		return Float.intBitsToFloat(getInt());
	}

	@Override
	public ByteBuffer putFloat(float value) {
		putInt(Float.floatToIntBits(value));
		return this;
	}

	@Override
	public float getFloat(int index) {
		return Float.intBitsToFloat(getInt(index));
	}

	@Override
	public ByteBuffer putFloat(int index, float value) {
		putInt(index, Float.floatToIntBits(value));
		return this;
	}

	@Override
	public double getDouble() {
		return Double.longBitsToDouble(getLong());
	}

	@Override
	public ByteBuffer putDouble(double value) {
		putLong(Double.doubleToLongBits(value));
		return this;
	}

	@Override
	public double getDouble(int index) {
		return Double.longBitsToDouble(getLong(index));
	}

	@Override
	public ByteBuffer putDouble(int index, double value) {
		putLong(index, Double.doubleToLongBits(value));
		return this;
	}

	@Override
	public boolean isReadOnly() {
		return read_only;
	}

	@Override
	public CharBuffer asCharBuffer() {
		return new ViewCharBuffer(array, array_offset + position, remaining() / 2, read_only);
	}

	@Override
	public ShortBuffer asShortBuffer() {
		return new ViewShortBuffer(array, array_offset + position, remaining() / 2, read_only);
	}

	@Override
	public IntBuffer asIntBuffer() {
		return new ViewIntBuffer(array, array_offset + position, remaining() / 2, read_only);
	}

	@Override
	public LongBuffer asLongBuffer() {
		return new ViewLongBuffer(array, array_offset + position, remaining() / 2, read_only);
	}

	@Override
	public FloatBuffer asFloatBuffer() {
		return new ViewFloatBuffer(array, array_offset + position, remaining() / 2, read_only);
	}

	@Override
	public DoubleBuffer asDoubleBuffer() {
		return new ViewDoubleBuffer(array, array_offset + position, remaining() / 2, read_only);
	}
}
