package java.nio;

final class ViewIntBuffer extends IntBuffer {
	private final boolean read_only;
	private final byte[] byte_array;
	private final ByteOrder order;

	public ViewIntBuffer(byte[] array, int array_offset, int capacity, ByteOrder order, boolean read_only) {
		super(capacity, null, array_offset);
		this.byte_array = array;
		this.order = order;
		this.read_only = read_only;
	}

	private ViewIntBuffer(byte[] array, int array_offset, int capacity, int position, int limit, ByteOrder order,
			boolean read_only) {
		super(capacity, null, array_offset);
		this.byte_array = array;
		this.order = order;
		this.position = position;
		this.limit = limit;
		this.read_only = read_only;
	}

	@Override
	public IntBuffer slice() {
		return new ViewIntBuffer(byte_array, array_offset + position, remaining(), 0, remaining(), order, read_only);
	}

	@Override
	public IntBuffer duplicate() {
		IntBuffer b = new ViewIntBuffer(byte_array, array_offset, capacity, position, limit, order, read_only);
		b.mark = mark;
		return b;
	}

	@Override
	public IntBuffer asReadOnlyBuffer() {
		IntBuffer b = new ViewIntBuffer(byte_array, array_offset, capacity, position, limit, order, true);
		b.mark = mark;
		return b;
	}

	@Override
	public int get() {
		if (position < limit) {
			return order.getInt(byte_array, array_offset + 4 * (position++));
		}
		throw new BufferUnderflowException();
	}

	private void checkReadOnly() {
		if (read_only) {
			throw new ReadOnlyBufferException();
		}
	}

	@Override
	public IntBuffer put(int b) {
		checkReadOnly();
		if (position < limit) {
			order.putInt(byte_array, array_offset + 4 * (position++), b);
			return this;
		}
		throw new BufferOverflowException();
	}

	@Override
	public int get(int index) {
		if (index < 0 || index >= limit) {
			throw new IndexOutOfBoundsException();
		}
		return order.getInt(byte_array, array_offset + 4 * index);
	}

	@Override
	public IntBuffer put(int index, int b) {
		checkReadOnly();
		if (index < 0 || index >= limit) {
			throw new IndexOutOfBoundsException();
		}
		order.putInt(byte_array, array_offset + 4 * index, b);
		return this;
	}

	@Override
	public IntBuffer get(int[] dst, int offset, int length) {
		return super.get(dst, offset, length); // TODO: optimize
	}

	@Override
	public IntBuffer get(int[] dst) {
		return super.get(dst); // TODO: optimize
	}

	@Override
	public IntBuffer put(IntBuffer src) {
		return super.put(src); // TODO: optimize
	}

	@Override
	public IntBuffer put(int[] src, int offset, int length) {
		return super.put(src, offset, length); // TODO: optimize
	}

	@Override
	public IntBuffer compact() {
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
