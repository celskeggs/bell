package java.io;

public class StringWriter extends Writer {

	private final StringBuffer sb;

	public StringWriter() {
		sb = new StringBuffer();
	}

	public StringWriter(int initialSize) {
		sb = new StringBuffer(initialSize);
	}

	@Override
	public void write(int c) throws IOException {
		sb.append((char) c);
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		sb.append(cbuf, off, len);
	}

	public void write(String str) {
		sb.append(str);
	}

	public void write(String str, int off, int len) {
		sb.append(str, off, off + len);
	}

	public StringWriter append(CharSequence csq) {
		sb.append(csq);
		return this;
	}

	public StringWriter append(CharSequence csq, int start, int end) {
		sb.append(csq, start, end);
		return this;
	}
	
	public StringWriter append(char c) {
		sb.append(c);
		return this;
	}

	@Override
	public void flush() throws IOException {
		// do nothing
	}

	@Override
	public void close() throws IOException {
		// do nothing
	}
	
	public StringBuffer getBuffer() {
		return sb;
	}
	
	public String toString() {
		return sb.toString();
	}
}
