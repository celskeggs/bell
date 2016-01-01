package java.io;

public class ByteArrayInputStream extends InputStream {
	protected byte[] buf;

	protected int pos;

	protected int mark;

	protected int count;

	public ByteArrayInputStream(byte[] buf) {
		this.buf = buf;
		this.pos = 0;
		this.count = buf.length;
		this.mark = 0;
	}

	public ByteArrayInputStream(byte[] buf, int offset, int length) {
		if (offset < 0 || length < 0 || offset > buf.length || offset + length < 0) {
			throw new IllegalArgumentException();
		}
		this.buf = buf;
		this.pos = offset;
		this.count = Math.min(buf.length, offset + length);
		this.mark = offset;
	}

	@Override
	public int read() {
		if (pos >= count) {
			return -1;
		}
		return buf[pos++] & 0xFF;
	}

	@Override
	public int read(byte[] b, int off, int len) {
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
	public long skip(long n) {
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
	public int available() {
		return count - pos;
	}

	@Override
	public boolean markSupported() {
		return true;
	}

	@Override
	public void mark(int readAheadLimit) {
		mark = pos;
	}

	@Override
	public void reset() {
		pos = mark;
	}

	@Override
	public void close() throws IOException {
		// do nothing
	}
}
