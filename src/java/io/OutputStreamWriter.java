package java.io;

import vm.CharacterCoder;

public class OutputStreamWriter extends Writer {

	private final OutputStream out;
	private final String charsetName;
	private boolean closed = false;

	public OutputStreamWriter(OutputStream out) {
		this(out, CharacterCoder.DEFAULT_ENCODING);
	}

	public OutputStreamWriter(OutputStream out, String charsetName) {
		this.out = out;
		this.charsetName = charsetName;
	}

	@Override
	public void write(int c) throws IOException {
		if (closed) {
			throw new IOException("OutputStreamWriter is already closed!");
		}
		this.write(new char[] {(char) c}, 0, 1);
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		if (closed) {
			throw new IOException("OutputStreamWriter is already closed!");
		}
		this.out.write(CharacterCoder.encode(cbuf, off, len, charsetName));
	}

	@Override
	public void write(String str, int off, int len) throws IOException {
		if (closed) {
			throw new IOException("OutputStreamWriter is already closed!");
		}
		char[] out = new char[len];
		System.arraycopy(str, off, out, 0, len);
		this.write(out);
	}

	@Override
	public void flush() throws IOException {
		if (closed) {
			throw new IOException("OutputStreamWriter is already closed!");
		}
		out.flush();
	}

	@Override
	public void close() throws IOException {
		closed = true;
		out.close();
	}

	public String getEncoding() {
		return closed ? null : charsetName;
	}
}
