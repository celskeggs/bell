package java.nio;

import java.io.IOException;

public abstract class CharBuffer extends Buffer implements Comparable<CharBuffer>, Appendable, CharSequence, Readable {

	final char[] array;
	final int array_offset;

	CharBuffer(int capacity, char[] array, int array_offset) {
		super(capacity);
		this.array = array;
		this.array_offset = array_offset;
	}

	public static CharBuffer allocate(int capacity) {
		return wrap(new char[capacity], 0, capacity);
	}

	public static CharBuffer wrap(char[] array, int offset, int length) {
		if (offset < 0 || offset > array.length || length < 0 || length > array.length - offset) {
			throw new IndexOutOfBoundsException();
		}
		return new ArrayCharBuffer(array, offset, length);
	}

	public static CharBuffer wrap(char[] array) {
		return wrap(array, 0, array.length);
	}

	public int read(CharBuffer target) throws IOException {
		// TODO: override this in subclasses
		// TODO: optimize?
		int count = 0;
		while (hasRemaining() && target.hasRemaining()) {
			target.put(get());
			count++;
		}
		return count;
	}

	public static CharBuffer wrap(CharSequence csq, int start, int end) {
		if (start < 0 || start > csq.length() || end < start || end > csq.length()) {
			throw new IndexOutOfBoundsException();
		}
		return new SequenceCharBuffer(csq, start, end - start);
	}

	public static CharBuffer wrap(CharSequence csq) {
		return wrap(csq, 0, csq.length());
	}

	public abstract CharBuffer slice();

	public abstract CharBuffer duplicate();

	public abstract CharBuffer asReadOnlyBuffer();

	public abstract char get();

	public abstract CharBuffer put(char c);

	public abstract char get(int index);

	public abstract CharBuffer put(int index, char c);

	public CharBuffer get(char[] dst, int offset, int length) {
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

	public CharBuffer get(char[] dst) {
		get(dst, 0, dst.length);
		return this;
	}

	public CharBuffer put(CharBuffer src) {
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

	public CharBuffer put(char[] src, int offset, int length) {
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

	public final CharBuffer put(char[] src) {
		this.put(src, 0, src.length);
		return this;
	}

	public CharBuffer put(String src, int start, int end) {
		if (end - start > remaining()) {
			throw new BufferOverflowException();
		}
		// TODO: I don't see the precondition start <= end specified ... should
		// it be included?
		if (start < 0 || start > src.length() || end < 0 || end > src.length()) {
			throw new IndexOutOfBoundsException();
		}
		// TODO: override this in subclasses
		// TODO: optimize?
		for (int i = start; i < end; i++) {
			put(src.charAt(i));
		}
		return this;
	}

	public final CharBuffer put(String src) {
		put(src, 0, src.length());
		return this;
	}

	public final boolean hasArray() {
		return array != null && !isReadOnly();
	}

	public final char[] array() {
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

	public abstract CharBuffer compact();

	public abstract boolean isDirect();

	public int hashCode() {
		// TODO: something better?
		int hash = 1;
		for (int i = position; i < limit; i++) {
			hash = (hash * 9) ^ ((get(i) & 0xFFFF) * (i + 1));
		}
		return hash;
	}

	public boolean equals(Object ob) {
		if (ob instanceof CharBuffer) {
			CharBuffer b = (CharBuffer) ob;
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

	public int compareTo(CharBuffer that) {
		int end = Math.min(remaining(), that.remaining());
		for (int i = 0; i < end; i++) {
			int diff = this.get(this.position + i) - that.get(that.position + i);
			if (diff != 0) {
				return diff;
			}
		}
		return remaining() - that.remaining();
	}

	public String toString() {
		// TODO: make sure this doesn't throw away the mark
		char[] chrs = new char[remaining()];
		int p = position();
		get(chrs);
		position(p);
		return new String(chrs);
	}

	public final int length() {
		return remaining();
	}

	public final char charAt(int index) {
		if (index < 0 || index >= remaining()) {
			throw new IndexOutOfBoundsException();
		}
		return get(position() + index);
	}

	public abstract CharBuffer subSequence(int start, int end);

	public CharBuffer append(CharSequence csq) {
		this.put(csq.toString());
		return this;
	}

	public CharBuffer append(CharSequence csq, int start, int end) {
		this.put(csq.subSequence(start, end).toString());
		return this;
	}

	public CharBuffer append(char c) {
		put(c);
		return this;
	}

	public abstract ByteOrder order();
}
