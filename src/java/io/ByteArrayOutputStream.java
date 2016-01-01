package java.io;

import java.util.Arrays;

public class ByteArrayOutputStream extends OutputStream {

	protected byte[] buf;

	protected int count;

	public ByteArrayOutputStream() {
		this(32);
	}

	public ByteArrayOutputStream(int size) {
		if (size < 0) {
			throw new IllegalArgumentException();
		}
		this.buf = new byte[size];
		count = 0;
	}

	@Override
	public void write(int b) {
		if (count >= buf.length) {
			buf = Arrays.copyOf(buf, count * 2 + 8);
		}
		buf[count++] = (byte) b;
	}

	@Override
	public void write(byte[] b, int off, int len) {
		if (off < 0 || len < 0 || off + len > b.length) {
			throw new IndexOutOfBoundsException();
		}
		if (count + len > buf.length) {
			buf = Arrays.copyOf(buf, Math.min(count * 2, count + len) + 8);
		}
		System.arraycopy(b, off, buf, count, len);
		count += len;
	}

	public void writeTo(OutputStream out) throws IOException {
		out.write(buf, 0, count);
	}

	public void reset() {
		count = 0;
	}

	public byte[] toByteArray() {
		return Arrays.copyOf(buf, count);
	}

	public int size() {
		return count;
	}

	public String toString() {
		// TODO: make sure this doesn't throw on malformed characters
		return new String(buf);
	}

	public String toString(String charsetName) throws UnsupportedEncodingException {
		// TODO: make sure this doesn't throw on malformed characters
		return new String(buf, charsetName);
	}

	@Deprecated
	public String toString(int hibyte) {
		char[] chrs = new char[count];
		for (int i = 0; i < count; i++) {
			chrs[i] = (char) (((hibyte & 0xFF) << 8) | (buf[i] & 0xFF));
		}
		return new String(chrs);
	}

	public void close() throws IOException {
		// Do nothing.
	}
}
