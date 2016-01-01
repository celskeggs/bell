package java.io;

public abstract class Writer implements Appendable, Closeable, Flushable {

	// TODO: should this not be final?
	protected final Object lock;

	protected Writer() {
		this.lock = this;
	}

	protected Writer(Object lock) {
		this.lock = lock;
	}

	public void write(int c) throws IOException {
		char[] cb = new char[1];
		cb[0] = (char) c;
		write(cb, 0, 1);
	}

	public void write(char[] cbuf) throws IOException {
		write(cbuf, 0, cbuf.length);
	}

	public abstract void write(char[] cbuf, int off, int len) throws IOException;

	public void write(String str) throws IOException {
		write(str.toCharArray());
	}

	public void write(String str, int off, int len) throws IOException {
		if (off < 0 || len < 0 || off + len < 0 || off + len > str.length()) {
			throw new IndexOutOfBoundsException();
		}
		write(str.substring(off, off + len).toCharArray());
	}

	public Writer append(CharSequence csq) throws IOException {
		if (csq == null) {
			csq = "null";
		}
		write(csq.toString());
		return this;
	}

	public Writer append(CharSequence csq, int start, int end) throws IOException {
		if (csq == null) {
			csq = "null";
		}
		if (start < 0 || end < 0 || start > end || end > csq.length()) {
			throw new IndexOutOfBoundsException();
		}
		write(csq.subSequence(start, end).toString());
		return this;
	}

	public Writer append(char c) throws IOException {
		write(c);
		return this;
	}

	public abstract void flush() throws IOException;

	public abstract void close() throws IOException;
}
