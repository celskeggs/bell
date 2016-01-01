package java.io;

public class CharArrayReader extends Reader {
	protected char[] buf;

	protected int pos;

	protected int markedPos;

	protected int count;

	public CharArrayReader(char[] buf) {
		this.buf = buf;
		this.pos = 0;
		this.markedPos = 0;
		this.count = buf.length;
	}

	public CharArrayReader(char[] buf, int offset, int length) {
		if (offset < 0 || length < 0 || offset > buf.length || offset + length < 0) {
			throw new IllegalArgumentException();
		}
		this.buf = buf;
		this.pos = offset;
		this.markedPos = offset;
		this.count = Math.min(offset + length, buf.length);
	}

	// TODO: synchronization?

	@Override
	public int read() throws IOException {
		if (buf == null) {
			throw new IOException("stream is closed!");
		}
		if (pos >= count) {
			return -1;
		}
		return buf[pos++];
	}

	@Override
	public int read(char[] b, int off, int len) throws IOException {
		if (buf == null) {
			throw new IOException("stream is closed!");
		}
		if (off < 0 || len < 0 || len > b.length - off) {
			throw new IndexOutOfBoundsException();
		}
		if (pos >= count) {
			return -1;
		}
		int to_read = Math.min(len, count - pos);
		System.arraycopy(buf, pos, b, off, to_read);
		return to_read;
	}

	@Override
	public long skip(long n) throws IOException {
		if (buf == null) {
			throw new IOException("stream is closed!");
		}
		if (n < 0) {
			throw new IllegalArgumentException();
		}
		// the cast to int works because count - pos is an integer, so the value
		// can be no bigger than an int
		int actual = (int) Math.min(n, count - pos);
		pos += actual;
		return actual;
	}

	@Override
	public boolean ready() throws IOException {
		if (buf == null) {
			throw new IOException("stream is closed!");
		}
		return true;
	}

	@Override
	public boolean markSupported() {
		return true;
	}

	@Override
	public void mark(int readAheadLimit) throws IOException {
		if (buf == null) {
			throw new IOException("stream is closed!");
		}
		markedPos = pos;
	}

	@Override
	public void reset() throws IOException {
		if (buf == null) {
			throw new IOException("stream is closed!");
		}
		pos = markedPos;
	}

	@Override
	public void close() {
		buf = null;
	}
}
