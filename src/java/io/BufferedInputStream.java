package java.io;

import com.celskeggs.bell.support.IncompleteImplementationError;

public class BufferedInputStream extends FilterInputStream {
	protected volatile byte[] buf;
	protected int count;
	// TODO: marklimit, markpos
	protected int pos;

	public BufferedInputStream(InputStream in) {
		this(in, 8192);
	}

	public BufferedInputStream(InputStream in, int size) {
		super(in);
		if (size <= 0) {
			throw new IllegalArgumentException();
		}
		buf = new byte[size];
	}

	// throws out the current buffer contents and starts again
	private boolean tryFillBuffer() throws IOException {
		int c = in.read(buf);
		if (c == -1) {
			pos = count = 0;
			return false;
		}
		count = c;
		pos = 0;
		return true;
	}

	public int read() throws IOException {
		if (pos < count || tryFillBuffer()) {
			return buf[pos++] & 0xFF;
		} else {
			return -1;
		}
	}

	public int read(byte[] b, int off, int len) throws IOException {
		// TODO: pass through larger reads
		if (len < 0) {
			throw new IllegalArgumentException();
		}
		if (len == 0) {
			return 0;
		}
		int actually_read = 0;
		while (true) {
			if (pos < count) {
				int available = count - pos;
				if (available >= len) {
					// we're done!
					System.arraycopy(buf, pos, b, off, len);
					pos += len;
					return actually_read + len;
				}
				// give them everything we've got
				System.arraycopy(buf, pos, b, off, available);
				pos = count;
				off += available;
				len -= available;
				actually_read += available;
			}
			if (actually_read == 0) {
				if (!tryFillBuffer()) {
					return -1;
				}
			} else {
				if (in.available() == 0 || !tryFillBuffer()) {
					return actually_read;
				}
			}
			// in this case, we did get more in the buffer!
			// we go around again to use it.
		}
	}

	public long skip(long n) throws IOException {
		// TODO: should this use the buffer?
		if (n < 0) {
			throw new IllegalArgumentException();
		}
		if (n == 0) {
			return 0;
		}
		if (pos < count) {
			int available = count - pos;
			if (available >= n) {
				// we're done!
				pos += n;
				return n;
			}
			// everything we've got
			pos = count;
			return available + in.skip(n - available);
		}
		return in.skip(n);
	}

	public int available() throws IOException {
		return (count - pos) + in.available();
	}

	public void mark(int readlimit) {
		throw new IncompleteImplementationError();
	}

	public void reset() throws IOException {
		throw new IncompleteImplementationError();
	}

	public boolean markSupported() {
		return true; // hah
	}

	public void close() throws IOException {
		in.close();
		// force any further reads to dispatch to the base stream, and so get
		// the appropriate already-closed exception
		count = pos = 0;
	}
}
