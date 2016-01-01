package java.nio;

import java.io.IOException;

final class ArrayCharBuffer extends CharBuffer {

	private final boolean read_only;

	public ArrayCharBuffer(char[] array, int offset, int length) {
		super(array.length, array, 0);
		position = offset;
		limit = offset + length;
		read_only = false;
	}

	private ArrayCharBuffer(char[] array, int array_offset, int capacity, int position, int limit, boolean read_only) {
		super(capacity, array, array_offset);
		this.position = position;
		this.limit = limit;
		this.read_only = read_only;
	}

	@Override
	public CharBuffer slice() {
		return new ArrayCharBuffer(array, array_offset + position(), remaining(), 0, remaining(), read_only);
	}

	@Override
	public CharBuffer duplicate() {
		CharBuffer b = new ArrayCharBuffer(array, array_offset, capacity, position, limit, read_only);
		b.mark = mark;
		return b;
	}

	@Override
	public CharBuffer subSequence(int start, int end) {
		if (start < 0 || start > remaining() || end < start || end > remaining()) {
			throw new IndexOutOfBoundsException();
		}
		// TODO: is mark behavior correct?
		CharBuffer b = new ArrayCharBuffer(array, array_offset, capacity, position + start, position + end, read_only);
		b.mark = mark;
		return b;
	}

	@Override
	public CharBuffer asReadOnlyBuffer() {
		CharBuffer b = new ArrayCharBuffer(array, array_offset, capacity, position, limit, true);
		b.mark = mark;
		return b;
	}

	public int read(CharBuffer target) throws IOException {
		int mrem = remaining();
		if (mrem == 0) {
			return -1;
		}
		int count = Math.min(mrem, target.remaining());
		target.put(array, array_offset + position, count);
		return count;
	}

	@Override
	public char get() {
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
	public CharBuffer put(char b) {
		checkReadOnly();
		if (position < limit) {
			array[array_offset + position++] = b;
			return this;
		}
		throw new BufferOverflowException();
	}

	@Override
	public char get(int index) {
		if (index < 0 || index >= limit) {
			throw new IndexOutOfBoundsException();
		}
		return array[array_offset + index];
	}

	@Override
	public CharBuffer put(int index, char b) {
		checkReadOnly();
		if (index < 0 || index >= limit) {
			throw new IndexOutOfBoundsException();
		}
		array[array_offset + index] = b;
		return this;
	}

	@Override
	public CharBuffer get(char[] dst, int offset, int length) {
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
	public CharBuffer get(char[] dst) {
		if (dst.length > remaining()) {
			throw new BufferUnderflowException();
		}
		System.arraycopy(array, array_offset + position, dst, 0, dst.length);
		position += dst.length;
		return this;
	}

	@Override
	public CharBuffer put(CharBuffer src) {
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
	public CharBuffer put(char[] src, int offset, int length) {
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
	public CharBuffer compact() {
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
