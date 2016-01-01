package java.io;

import java.util.Arrays;

public class CharArrayWriter extends Writer {

	protected char[] buf;

	protected int count;

	public CharArrayWriter() {
		this(32);
	}

	public CharArrayWriter(int initialSize) {
		if (initialSize < 0) {
			throw new IllegalArgumentException();
		}
		this.buf = new char[initialSize];
		count = 0;
	}

	// TODO: synchronization?

	@Override
	public void write(int c) {
		if (count >= buf.length) {
			buf = Arrays.copyOf(buf, count * 2 + 8);
		}
		buf[count++] = (char) c;
	}

	@Override
	public void write(char[] c, int off, int len) {
		if (count + len > buf.length) {
			buf = Arrays.copyOf(buf, Math.max(count * 2, count + len) + 8);
		}
		System.arraycopy(c, off, buf, count, len);
		count += len;
	}

	@Override
	public void write(char[] cbuf) {
		// TODO: Is it okay that I'm overriding this when it isn't in the docs?
		write(cbuf, 0, cbuf.length);
	}

	@Override
	public void write(String str) {
		// TODO: Is it okay that I'm overriding this when it isn't in the docs?
		write(str, 0, str.length());
	}

	@Override
	public void write(String str, int off, int len) {
		if (off < 0 || len < 0 || off + len < 0 || off + len > str.length()) {
			throw new IndexOutOfBoundsException();
		}
		if (count + len > buf.length) {
			buf = Arrays.copyOf(buf, Math.max(count * 2, count + len) + 8);
		}
		str.getChars(off, off + len, buf, count);
		count += len;
	}

	public void writeTo(Writer out) throws IOException {
		out.write(buf, 0, count);
	}

	public CharArrayWriter append(CharSequence csq) {
		if (csq == null) {
			csq = "null";
		}
		write(csq.toString());
		return this;
	}

	public CharArrayWriter append(CharSequence csq, int start, int end) {
		if (csq == null) {
			csq = "null";
		}
		if (start < 0 || end < 0 || start > end || end > csq.length()) {
			throw new IndexOutOfBoundsException();
		}
		write(csq.subSequence(start, end).toString());
		return this;
	}

	public CharArrayWriter append(char c) {
		write(c);
		return this;
	}

	public void reset() {
		count = 0;
	}

	public char[] toCharArray() {
		return Arrays.copyOf(buf, count);
	}

	public int size() {
		return count;
	}

	public String toString() {
		return new String(buf, 0, count);
	}

	public void flush() {
		// do nothing
	}

	public void close() {
		// do nothing
	}
}
