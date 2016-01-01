package java.nio;

final class ViewFloatBuffer extends FloatBuffer {
	private final boolean read_only;
	private final byte[] byte_array;
	private final ByteOrder order;

	public ViewFloatBuffer(byte[] array, int array_offset, int capacity, ByteOrder order, boolean read_only) {
		super(capacity, null, array_offset);
		this.byte_array = array;
		this.order = order;
		this.read_only = read_only;
	}

	private ViewFloatBuffer(byte[] array, int array_offset, int capacity, int position, int limit, ByteOrder order,
			boolean read_only) {
		super(capacity, null, array_offset);
		this.byte_array = array;
		this.order = order;
		this.position = position;
		this.limit = limit;
		this.read_only = read_only;
	}

	@Override
	public FloatBuffer slice() {
		return new ViewFloatBuffer(byte_array, array_offset + position, remaining(), 0, remaining(), order, read_only);
	}

	@Override
	public FloatBuffer duplicate() {
		FloatBuffer b = new ViewFloatBuffer(byte_array, array_offset, capacity, position, limit, order, read_only);
		b.mark = mark;
		return b;
	}

	@Override
	public FloatBuffer asReadOnlyBuffer() {
		FloatBuffer b = new ViewFloatBuffer(byte_array, array_offset, capacity, position, limit, order, true);
		b.mark = mark;
		return b;
	}

	@Override
	public float get() {
		if (position < limit) {
			return Float.intBitsToFloat(order.getInt(byte_array, array_offset + 4 * (position++)));
		}
		throw new BufferUnderflowException();
	}

	private void checkReadOnly() {
		if (read_only) {
			throw new ReadOnlyBufferException();
		}
	}

	@Override
	public FloatBuffer put(float b) {
		checkReadOnly();
		if (position < limit) {
			order.putInt(byte_array, array_offset + 4 * (position++), Float.floatToIntBits(b));
			return this;
		}
		throw new BufferOverflowException();
	}

	@Override
	public float get(int index) {
		if (index < 0 || index >= limit) {
			throw new IndexOutOfBoundsException();
		}
		return Float.intBitsToFloat(order.getInt(byte_array, array_offset + 4 * index));
	}

	@Override
	public FloatBuffer put(int index, float b) {
		checkReadOnly();
		if (index < 0 || index >= limit) {
			throw new IndexOutOfBoundsException();
		}
		order.putInt(byte_array, array_offset + 4 * index, Float.floatToIntBits(b));
		return this;
	}

	@Override
	public FloatBuffer get(float[] dst, int offset, int length) {
		return super.get(dst, offset, length); // TODO: optimize
	}

	@Override
	public FloatBuffer get(float[] dst) {
		return super.get(dst); // TODO: optimize
	}

	@Override
	public FloatBuffer put(FloatBuffer src) {
		return super.put(src); // TODO: optimize
	}

	@Override
	public FloatBuffer put(float[] src, int offset, int length) {
		return super.put(src, offset, length); // TODO: optimize
	}

	@Override
	public FloatBuffer compact() {
		checkReadOnly();
		System.arraycopy(byte_array, array_offset + 4 * position, byte_array, array_offset, 4 * (limit - position));
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
	public boolean isReadOnly() {
		return read_only;
	}

	@Override
	public ByteOrder order() {
		return order;
	}
}
