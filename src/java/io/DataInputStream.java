package java.io;

public class DataInputStream extends FilterInputStream implements DataInput {

	public DataInputStream(InputStream in) {
		super(in);
	}

	public final int read(byte[] b) throws IOException {
		return in.read(b);
	}

	public final int read(byte[] b, int off, int len) throws IOException {
		return in.read(b, off, len);
	}

	public final void readFully(byte[] b) throws IOException {
		readFully(b, 0, b.length);
	}

	public final void readFully(byte[] b, int off, int len) throws IOException {
		int end = off + len;
		while (off < end) {
			int r = in.read(b, off, end - off);
			if (r == -1) {
				throw new EOFException();
			}
			off += r;
		}
	}

	public final int skipBytes(int n) throws IOException {
		long total = in.skip(n);
		if (total != (int) total) {
			throw new RuntimeException("Invalid implementation of skip");
		}
		return (int) total;
	}

	public final boolean readBoolean() throws IOException {
		return readByte() != 0;
	}

	public final int readUnsignedByte() throws IOException {
		int i = in.read();
		if (i == -1) {
			throw new EOFException();
		}
		return i & 0xFF;
	}

	public final byte readByte() throws IOException {
		return (byte) readUnsignedByte();
	}

	public final int readUnsignedShort() throws IOException {
		int a = readUnsignedByte();
		return (a << 8) | readUnsignedByte();
	}

	public final short readShort() throws IOException {
		return (short) readUnsignedShort();
	}

	public final char readChar() throws IOException {
		return (char) readUnsignedShort();
	}

	public final int readInt() throws IOException {
		return (readUnsignedShort() << 16) | readUnsignedShort();
	}

	public final long readLong() throws IOException {
		return (readUnsignedShort() << 48) | (readUnsignedShort() << 32) | (readUnsignedShort() << 16)
				| readUnsignedShort();
	}

	public final double readDouble() throws IOException {
		return Double.longBitsToDouble(readLong());
	}

	public final float readFloat() throws IOException {
		return Float.intBitsToFloat(readInt());
	}

	// readLine not implemented because deprecated

	public final String readUTF() throws IOException {
		return readUTF(this);
	}

	public static String readUTF(DataInput in) throws IOException {
		int raw_length = in.readUnsignedShort();
		byte[] raw = new byte[raw_length];
		char[] chars = new char[raw_length];
		int real_length = 0;
		for (int i = 0; i < raw_length; i++) {
			byte b = raw[i];
			if ((b & 0x80) == 0) {
				chars[real_length++] = (char) (b & 0xFF);
			} else if ((b & 0x40) == 0) {
				throw new UTFDataFormatException("Invalid initial byte in group: " + b);
			} else if ((b & 0x20) == 0) {
				if (i + 1 >= raw_length) {
					throw new UTFDataFormatException("Invalid unterminated group starting at " + b);
				}
				byte b2 = raw[i + 1];
				if ((b2 & 0xC0) != 0x80) {
					throw new UTFDataFormatException("Invalid second byte in group: " + b + ", " + b2);
				}
				chars[real_length++] = (char) (((b & 0x1F) << 6) | (b2 & 0x3F));
			} else if ((b & 0x10) != 0) {
				throw new UTFDataFormatException(
						"Invalid initial byte in group: " + b + " (perhaps you aren't using Java's modified UTF-8?)");
			} else if (i + 2 >= raw_length) {
				throw new UTFDataFormatException("Invalid unterminated group starting at " + b);
			} else {
				byte b2 = raw[i + 1];
				byte b3 = raw[i + 2];
				if ((b2 & 0xC0) != 0x80) {
					throw new UTFDataFormatException("Invalid second byte in group: " + b + ", " + b2 + ", " + b3);
				}
				if ((b3 & 0xC0) != 0x80) {
					throw new UTFDataFormatException("Invalid third byte in group: " + b + ", " + b2 + ", " + b3);
				}
				chars[real_length++] = (char) (((b & 0x0F) << 12) | ((b2 & 0x3F) << 6) | (b3 & 0x3F));
			}
		}
		return new String(chars, 0, real_length);
	}
}
