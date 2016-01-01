package java.io;

public class FilterWriter extends Writer {

	protected Writer out;

	protected FilterWriter(Writer out) {
		if (out == null) {
			throw new NullPointerException();
		}
		this.out = out;
	}

	@Override
	public void write(int c) throws IOException {
		out.write(c);
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		out.write(cbuf, off, len);
	}

	@Override
	public void write(String str, int off, int len) throws IOException {
		out.write(str, off, len);
	}

	@Override
	public void flush() throws IOException {
		out.flush();
	}

	@Override
	public void close() throws IOException {
		out.close();
	}
}
