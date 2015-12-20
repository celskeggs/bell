package java.io;

public class FilterOutputStream extends OutputStream {
	protected OutputStream out;

	public FilterOutputStream(OutputStream out) {
		this.out = out;
	}

	public void write(int b) throws IOException {
		out.write(b);
	}

	public void close() throws IOException {
		out.close();
	}

	public void flush() throws IOException {
		out.flush();
	}

	public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	}

	public void write(byte[] b, int start, int count) throws IOException {
		out.write(b, start, count);
	}
}
