package java.io;

public class FilterReader extends Reader {

	protected Reader in;

	protected FilterReader(Reader in) {
		if (in == null) {
			throw new NullPointerException();
		}
		this.in = in;
	}

	@Override
	public int read() throws IOException {
		return in.read();
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		return in.read(cbuf, off, len);
	}

	@Override
	public long skip(long n) throws IOException {
		return in.skip(n);
	}

	@Override
	public boolean ready() throws IOException {
		return in.ready();
	}

	@Override
	public boolean markSupported() {
		return in.markSupported();
	}

	@Override
	public void mark(int readAheadLimit) throws IOException {
		in.mark(readAheadLimit);
	}

	@Override
	public void reset() throws IOException {
		in.reset();
	}

	@Override
	public void close() throws IOException {
		in.close();
	}
}
