package java.io;

public abstract class InputStream implements Closeable {
	public int available() throws IOException {
		return 0;
	}
	
	public void close() throws IOException {
		// do nothing
	}
	
	public void mark(int readlimit) {
		// do nothing
	}

	public void reset() throws IOException {
		throw new IOException("Not a markable stream.");
	}
	
	boolean markSupported() {
		return false;
	}

	public abstract int read() throws IOException;
	
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}
	
	public int read(byte[] b, int off, int len) throws IOException {
		for (int i = 0; i < len; i++) {
			int x = read();
			if (x == -1) {
				return i == 0 ? -1 : i;
			}
			b[off + i] = (byte) x;
		}
		return len;
	}

	// just dump everything into a shared byte array. concurrency issues do not matter.
	private static final byte[] temp = new byte[1024];
	
	public long skip(long n) throws IOException {
		long needed = n;
		while (n >= 1024) {
			int actual = read(temp, 0, 1024);
			if (actual < 0) {
				return needed - n;
			}
			n -= actual;
		}
		while (n > 0) {
			int actual = read(temp, 0, (int) n);
			if (actual < 0) {
				return needed - n;
			}
			n -= actual;
		}
		return 0;
	}
}
