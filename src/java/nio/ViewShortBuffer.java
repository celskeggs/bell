package java.nio;

final class ViewShortBuffer extends ShortBuffer {
	private final boolean read_only;
	private final byte[] byte_array;
	private final ByteOrder order;

	public ViewShortBuffer(byte[] array, int array_offset, int capacity, ByteOrder order, boolean read_only) {
		super(capacity, null, array_offset);
		this.byte_array = array;
		this.order = order;
		this.read_only = read_only;
	}

	private ViewShortBuffer(byte[] array, int array_offset, int capacity, int position, int limit, ByteOrder order,
			boolean read_only) {
		super(capacity, null, array_offset);
		this.byte_array = array;
		this.order = order;
		this.position = position;
		this.limit = limit;
		this.read_only = read_only;
	}

	@Override
	public ShortBuffer slice() {
		return new ViewShortBuffer(byte_array, array_offset + position, remaining(), 0, remaining(), order, read_only);
	}

	@Override
	public ShortBuffer duplicate() {
		ShortBuffer b = new ViewShortBuffer(byte_array, array_offset, capacity, position, limit, order, read_only);
		b.mark = mark;
		return b;
	}

	@Override
	public ShortBuffer asReadOnlyBuffer() {
		ShortBuffer b = new ViewShortBuffer(byte_array, array_offset, capacity, position, limit, order, true);
		b.mark = mark;
		return b;
	}

	@Override
	public short get() {
		if (position < limit) {
			return order.getShort(byte_array, array_offset + 2 * (position++));
		}
		throw new BufferUnderflowException();
	}

	private void checkReadOnly() {
		if (read_only) {
			throw new ReadOnlyBufferException();
		}
	}

	@Override
	public ShortBuffer put(short b) {
		checkReadOnly();
		if (position < limit) {
			order.putShort(byte_array, array_offset + 2 * (position++), b);
			return this;
		}
		throw new BufferOverflowException();
	}

	@Override
	public short get(int index) {
		if (index < 0 || index >= limit) {
			throw new IndexOutOfBoundsException();
		}
		return order.getShort(byte_array, array_offset + 2 * index);
	}

	@Override
	public ShortBuffer put(int index, short b) {
		checkReadOnly();
		if (index < 0 || index >= limit) {
			throw new IndexOutOfBoundsException();
		}
		order.putShort(byte_array, array_offset + 2 * index, b);
		return this;
	}

	@Override
	public ShortBuffer get(short[] dst, int offset, int length) {
		return super.get(dst, offset, length); // TODO: optimize
	}

	@Override
	public ShortBuffer get(short[] dst) {
		return super.get(dst); // TODO: optimize
	}

	@Override
	public ShortBuffer put(ShortBuffer src) {
		return super.put(src); // TODO: optimize
	}

	@Override
	public ShortBuffer put(short[] src, int offset, int length) {
		return super.put(src, offset, length); // TODO: optimize
	}

	@Override
	public ShortBuffer compact() {
		checkReadOnly();
		System.arraycopy(byte_array, array_offset + 2 * position, byte_array, array_offset, 2 * (limit - position));
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
