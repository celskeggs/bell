package java.nio;

final class ViewLongBuffer extends LongBuffer {
	private final boolean read_only;
	private final byte[] byte_array;
	private final ByteOrder order;

	public ViewLongBuffer(byte[] array, int array_offset, int capacity, ByteOrder order, boolean read_only) {
		super(capacity, null, array_offset);
		this.byte_array = array;
		this.order = order;
		this.read_only = read_only;
	}

	private ViewLongBuffer(byte[] array, int array_offset, int capacity, int position, int limit, ByteOrder order,
			boolean read_only) {
		super(capacity, null, array_offset);
		this.byte_array = array;
		this.order = order;
		this.position = position;
		this.limit = limit;
		this.read_only = read_only;
	}

	@Override
	public LongBuffer slice() {
		return new ViewLongBuffer(byte_array, array_offset + position, remaining(), 0, remaining(), order, read_only);
	}

	@Override
	public LongBuffer duplicate() {
		LongBuffer b = new ViewLongBuffer(byte_array, array_offset, capacity, position, limit, order, read_only);
		b.mark = mark;
		return b;
	}

	@Override
	public LongBuffer asReadOnlyBuffer() {
		LongBuffer b = new ViewLongBuffer(byte_array, array_offset, capacity, position, limit, order, true);
		b.mark = mark;
		return b;
	}

	@Override
	public long get() {
		if (position < limit) {
			return order.getLong(byte_array, array_offset + 8 * (position++));
		}
		throw new BufferUnderflowException();
	}

	private void checkReadOnly() {
		if (read_only) {
			throw new ReadOnlyBufferException();
		}
	}

	@Override
	public LongBuffer put(long b) {
		checkReadOnly();
		if (position < limit) {
			order.putLong(byte_array, array_offset + 8 * (position++), b);
			return this;
		}
		throw new BufferOverflowException();
	}

	@Override
	public long get(int index) {
		if (index < 0 || index >= limit) {
			throw new IndexOutOfBoundsException();
		}
		return order.getLong(byte_array, array_offset + 8 * index);
	}

	@Override
	public LongBuffer put(int index, long b) {
		checkReadOnly();
		if (index < 0 || index >= limit) {
			throw new IndexOutOfBoundsException();
		}
		order.putLong(byte_array, array_offset + 8 * index, b);
		return this;
	}

	@Override
	public LongBuffer get(long[] dst, int offset, int length) {
		return super.get(dst, offset, length); // TODO: optimize
	}

	@Override
	public LongBuffer get(long[] dst) {
		return super.get(dst); // TODO: optimize
	}

	@Override
	public LongBuffer put(LongBuffer src) {
		return super.put(src); // TODO: optimize
	}

	@Override
	public LongBuffer put(long[] src, int offset, int length) {
		return super.put(src, offset, length); // TODO: optimize
	}

	@Override
	public LongBuffer compact() {
		checkReadOnly();
		System.arraycopy(byte_array, array_offset + 8 * position, byte_array, array_offset, 8 * (limit - position));
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
