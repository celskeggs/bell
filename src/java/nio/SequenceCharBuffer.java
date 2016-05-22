package java.nio;

import java.io.IOException;

final class SequenceCharBuffer extends CharBuffer {

	private final CharSequence seq;

	public SequenceCharBuffer(CharSequence seq, int offset, int length) {
		super(seq.length(), null, 0);
		position = offset;
		limit = offset + length;
		this.seq = seq;
	}

	private SequenceCharBuffer(CharSequence seq, int array_offset, int capacity, int position, int limit) {
		super(capacity, null, array_offset);
		this.position = position;
		this.limit = limit;
		this.seq = seq;
	}

	@Override
	public CharBuffer slice() {
		return new SequenceCharBuffer(seq, array_offset + position(), remaining(), 0, remaining());
	}

	@Override
	public CharBuffer duplicate() {
		CharBuffer b = new SequenceCharBuffer(seq, array_offset, capacity, position, limit);
		b.mark = mark;
		return b;
	}

	@Override
	public CharBuffer subSequence(int start, int end) {
		if (start < 0 || start > remaining() || end < start || end > remaining()) {
			throw new IndexOutOfBoundsException();
		}
		// TODO: is mark behavior correct?
		CharBuffer b = new SequenceCharBuffer(seq, array_offset, capacity, position + start, position + end);
		b.mark = mark;
		return b;
	}

	@Override
	public CharBuffer asReadOnlyBuffer() {
		CharBuffer b = new SequenceCharBuffer(seq, array_offset, capacity, position, limit);
		b.mark = mark;
		return b;
	}

	public int read(CharBuffer target) throws IOException {
		int mrem = remaining();
		if (mrem == 0) {
			return -1;
		}
		int count = Math.min(mrem, target.remaining());
		// TODO: something more efficient?
		target.put(seq.subSequence(array_offset + position, array_offset + position + count).toString().toCharArray());
		return count;
	}

	@Override
	public char get() {
		if (position < limit) {
			return seq.charAt(array_offset + position++);
		}
		throw new BufferUnderflowException();
	}

	@Override
	public CharBuffer put(char b) {
		throw new ReadOnlyBufferException();
	}

	@Override
	public char get(int index) {
		if (index < 0 || index >= limit) {
			throw new IndexOutOfBoundsException();
		}
		return seq.charAt(array_offset + index);
	}

	@Override
	public CharBuffer put(int index, char b) {
		throw new ReadOnlyBufferException();
	}

	@Override
	public CharBuffer get(char[] dst, int offset, int length) {
		if (length > remaining()) {
			throw new BufferUnderflowException();
		}
		if (offset < 0 || offset > dst.length || length < 0 || length + offset > dst.length) {
			throw new IndexOutOfBoundsException();
		}
		// TODO: something more efficient?
		seq.subSequence(array_offset + position, array_offset + position + length).toString().getChars(0, length, dst,
				offset);
		position += length;
		return this;
	}

	@Override
	public CharBuffer get(char[] dst) {
		if (dst.length > remaining()) {
			throw new BufferUnderflowException();
		}
		// TODO: something more efficient?
		seq.subSequence(array_offset + position, array_offset + position + dst.length).toString().getChars(0,
				dst.length, dst, 0);
		position += dst.length;
		return this;
	}

	@Override
	public CharBuffer put(CharBuffer src) {
		throw new ReadOnlyBufferException();
	}

	@Override
	public CharBuffer put(char[] src, int offset, int length) {
		throw new ReadOnlyBufferException();
	}

	@Override
	public CharBuffer compact() {
		throw new ReadOnlyBufferException();
	}

	@Override
	public boolean isDirect() {
		return false;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public ByteOrder order() {
		return ByteOrder.nativeOrder();
	}
}
