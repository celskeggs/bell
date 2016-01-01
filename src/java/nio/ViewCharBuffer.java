package java.nio;

final class ViewCharBuffer extends CharBuffer {
	private final boolean read_only;
	private final byte[] byte_array;
	private final ByteOrder order;

	public ViewCharBuffer(byte[] array, int array_offset, int capacity, ByteOrder order, boolean read_only) {
		super(capacity, null, array_offset);
		this.byte_array = array;
		this.order = order;
		this.read_only = read_only;
	}

	private ViewCharBuffer(byte[] array, int array_offset, int capacity, int position, int limit, ByteOrder order, boolean read_only) {
		super(capacity, null, array_offset);
		this.byte_array = array;
		this.order = order;
		this.position = position;
		this.limit = limit;
		this.read_only = read_only;
	}

	@Override
	public CharBuffer slice() {
		return new ViewCharBuffer(byte_array, array_offset + position, remaining(), 0, remaining(), order, read_only);
	}

	@Override
	public CharBuffer duplicate() {
		CharBuffer b = new ViewCharBuffer(byte_array, array_offset, capacity, position, limit, order, read_only);
		b.mark = mark;
		return b;
	}

	@Override
	public CharBuffer subSequence(int start, int end) {
		if (start < 0 || start > remaining() || end < start || end > remaining()) {
			throw new IndexOutOfBoundsException();
		}
		// TODO: is mark behavior correct?
		CharBuffer b = new ViewCharBuffer(byte_array, array_offset, capacity, position + start, position + end, order, read_only);
		b.mark = mark;
		return b;
	}

	@Override
	public CharBuffer asReadOnlyBuffer() {
		CharBuffer b = new ViewCharBuffer(byte_array, array_offset, capacity, position, limit, order, true);
		b.mark = mark;
		return b;
	}

	@Override
	public char get() {
		if (position < limit) {
			return (char) order.getShort(byte_array, array_offset + 2 * (position++));
		}
		throw new BufferUnderflowException();
	}

	private void checkReadOnly() {
		if (read_only) {
			throw new ReadOnlyBufferException();
		}
	}

	@Override
	public CharBuffer put(char b) {
		checkReadOnly();
		if (position < limit) {
			order.putShort(byte_array, array_offset + 2 * (position++), (short) b);
			return this;
		}
		throw new BufferOverflowException();
	}

	@Override
	public char get(int index) {
		if (index < 0 || index >= limit) {
			throw new IndexOutOfBoundsException();
		}
		return (char) order.getShort(byte_array, array_offset + 2 * index);
	}

	@Override
	public CharBuffer put(int index, char b) {
		checkReadOnly();
		if (index < 0 || index >= limit) {
			throw new IndexOutOfBoundsException();
		}
		order.putShort(byte_array, array_offset + 2 * index, (short) b);
		return this;
	}

	@Override
	public CharBuffer get(char[] dst, int offset, int length) {
		return super.get(dst, offset, length); // TODO: optimize
	}

	@Override
	public CharBuffer get(char[] dst) {
		return super.get(dst); // TODO: optimize
	}

	@Override
	public CharBuffer put(CharBuffer src) {
		return super.put(src); // TODO: optimize
	}

	@Override
	public CharBuffer put(char[] src, int offset, int length) {
		return super.put(src, offset, length); // TODO: optimize
	}

	@Override
	public CharBuffer compact() {
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
