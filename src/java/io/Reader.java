package java.io;

import java.nio.CharBuffer;

public abstract class Reader implements Readable, Closeable {

	// TODO: should this not be final?
	protected final Object lock;

	protected Reader() {
		lock = this;
	}

	protected Reader(Object lock) {
		this.lock = lock;
	}

	public int read(CharBuffer target) throws IOException {
		char[] chrs = new char[Math.min(target.remaining(), 4096)];
		int actual = read(chrs, 0, chrs.length);
		if (actual < 0) {
			return -1;
		}
		target.put(chrs, 0, actual);
		return actual;
	}

	public int read() throws IOException {
		char[] c = new char[1];
		int count = read(c, 0, 1);
		if (count == 1) {
			return c[0] & 0xFFFF;
		} else if (count == -1) {
			return -1;
		} else {
			throw new IOException("Unexpected and invalid result from internal read: " + count);
		}
	}

	public int read(char[] cbuf) throws IOException {
		return read(cbuf, 0, cbuf.length);
	}

	public abstract int read(char[] cbuf, int off, int len) throws IOException;

	// concurrency issues do not matter because we don't care about the values
	private static final char[] dump = new char[4096];

	public long skip(long n) throws IOException {
		long total = 0;
		while (n > 0) {
			int count = read(dump, 0, (int) Math.min(4096, n));
			if (count < 0) {
				return total;
			}
			n -= count;
			total += count;
		}
		return total;
	}

	public boolean ready() throws IOException {
		return false;
	}

	public boolean markSupported() {
		return false;
	}

	public void mark(int readAheadLimit) throws IOException {
		throw new IOException("marks are not supported!");
	}

	public void reset() throws IOException {
		throw new IOException("marks are not supported!");
	}

	public abstract void close() throws IOException;
}
