package java.nio;

final class ArrayIntBuffer extends IntBuffer {

	private final boolean read_only;

	public ArrayIntBuffer(int[] array, int offset, int length) {
		super(array.length, array, 0);
		position = offset;
		limit = offset + length;
		read_only = false;
	}

	private ArrayIntBuffer(int[] array, int array_offset, int capacity, int position, int limit, boolean read_only) {
		super(capacity, array, array_offset);
		this.position = position;
		this.limit = limit;
		this.read_only = read_only;
	}

	@Override
	public IntBuffer slice() {
		return new ArrayIntBuffer(array, array_offset + position(), remaining(), 0, remaining(), read_only);
	}

	@Override
	public IntBuffer duplicate() {
		IntBuffer b = new ArrayIntBuffer(array, array_offset, capacity, position, limit, read_only);
		b.mark = mark;
		return b;
	}

	@Override
	public IntBuffer asReadOnlyBuffer() {
		IntBuffer b = new ArrayIntBuffer(array, array_offset, capacity, position, limit, true);
		b.mark = mark;
		return b;
	}

	@Override
	public int get() {
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
	public IntBuffer put(int b) {
		checkReadOnly();
		if (position < limit) {
			array[array_offset + position++] = b;
			return this;
		}
		throw new BufferOverflowException();
	}

	@Override
	public int get(int index) {
		if (index < 0 || index >= limit) {
			throw new IndexOutOfBoundsException();
		}
		return array[array_offset + index];
	}

	@Override
	public IntBuffer put(int index, int b) {
		checkReadOnly();
		if (index < 0 || index >= limit) {
			throw new IndexOutOfBoundsException();
		}
		array[array_offset + index] = b;
		return this;
	}

	@Override
	public IntBuffer get(int[] dst, int offset, int length) {
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

	@Override
	public IntBuffer get(int[] dst) {
		if (dst.length > remaining()) {
			throw new BufferUnderflowException();
		}
		System.arraycopy(array, array_offset + position, dst, 0, dst.length);
		position += dst.length;
		return this;
	}

	@Override
	public IntBuffer put(IntBuffer src) {
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

	@Override
	public IntBuffer put(int[] src, int offset, int length) {
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
	public IntBuffer compact() {
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
	public boolean isReadOnly() {
		return read_only;
	}

	@Override
	public ByteOrder order() {
		return ByteOrder.nativeOrder();
	}
}
