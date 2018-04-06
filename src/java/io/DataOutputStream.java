package java.io;

public class DataOutputStream extends FilterOutputStream implements DataOutput, AutoCloseable {
	protected int written = 0;

	public DataOutputStream(OutputStream out) {
		super(out);
	}

	public int size() {
		return written;
	}

	public void flush() throws IOException {
		out.flush();
	}

	public void write(int b) throws IOException {
		out.write(b);
		if (++written < 0) {
			written = Integer.MAX_VALUE;
		}
	}

	public void write(byte[] b, int start, int count) throws IOException {
		out.write(b, start, count);
		written += count;
		if (written < 0) {
			written = Integer.MAX_VALUE;
		}
	}

	public final void writeBoolean(boolean v) throws IOException {
		write(v ? 1 : 0);
	}

	public final void writeByte(int v) throws IOException {
		write(v);
	}

	public void writeShort(int v) throws IOException {
		write(v >> 8);
		write(v);
	}

	public void writeChar(int v) throws IOException {
		write(v >> 8);
		write(v);
	}

	public void writeInt(int v) throws IOException {
		write(v >> 24);
		write(v >> 16);
		write(v >> 8);
		write(v);
	}

	public void writeLong(long v) throws IOException {
		write((int) (v >> 56));
		write((int) (v >> 48));
		write((int) (v >> 40));
		write((int) (v >> 32));
		write((int) (v >> 24));
		write((int) (v >> 16));
		write((int) (v >> 8));
		write((int) v);
	}

	public void writeFloat(float v) throws IOException {
		writeInt(Float.floatToIntBits(v));
	}

	public void writeDouble(double v) throws IOException {
		writeLong(Double.doubleToLongBits(v));
	}

	public void writeBytes(String s) throws IOException {
		char[] chars = s.toCharArray();
		byte[] bytes = new byte[chars.length];
		for (int i = 0; i < chars.length; i++) {
			bytes[i] = (byte) chars[i];
		}
		write(bytes);
	}

	public void writeChars(String s) throws IOException {
		char[] chars = s.toCharArray();
		byte[] bytes = new byte[chars.length * 2];
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			bytes[2 * i + 0] = (byte) (c >> 8);
			bytes[2 * i + 1] = (byte) c;
		}
		write(bytes);
	}

	public void writeUTF(String s) throws IOException {
		char[] chars = s.toCharArray();
		if (chars.length > 65535) {
			throw new UTFDataFormatException("Too many characters (> 65535) to write as UTF!");
		}
		byte[] out = new byte[chars.length * 3];
		int bi = 0;
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (c >= 0x01 && c <= 0x7F) {
				out[bi++] = (byte) c;
			} else if (c <= 0x7FF) {
				out[bi++] = (byte) (((c >> 6) & 0x1F) | 0xC0);
				out[bi++] = (byte) ((c & 0x3F) | 0x80);
			} else {
				out[bi++] = (byte) (((c >> 12) & 0x0F) | 0xE0);
				out[bi++] = (byte) (((c >> 6) & 0x3F) | 0x80);
				out[bi++] = (byte) ((c & 0x3F) | 0x80);
			}
		}
		if (bi > 65535) {
			throw new UTFDataFormatException("Too many bytes (> 65535) required to encode characters as UTF!");
		}
		writeShort(bi);
		write(out, 0, bi);
	}
}
