package java.nio;

import com.celskeggs.support.IncompleteImplementationError;

public final class ByteOrder {
	public static final ByteOrder BIG_ENDIAN = new ByteOrder();
	public static final ByteOrder LITTLE_ENDIAN = new ByteOrder();

	private ByteOrder() {
	}

	public static ByteOrder nativeOrder() {
		throw new IncompleteImplementationError();
	}

	public String toString() {
		if (this == LITTLE_ENDIAN) {
			return "LITTLE_ENDIAN";
		} else {
			return "BIG_ENDIAN";
		}
	}

	short getShort(byte[] array, int i) {
		int f = array[i] & 0xFF;
		int s = array[i + 1] & 0xFF;
		// TODO: use dispatch better?
		if (this == ByteOrder.LITTLE_ENDIAN) {
			return (short) (f | (s << 8));
		} else {
			return (short) ((f << 8) | s);
		}
	}

	void putShort(byte[] array, int i, short value) {
		if (this == ByteOrder.LITTLE_ENDIAN) {
			array[i] = (byte) value;
			array[i + 1] = (byte) (value >> 8);
		} else {
			array[i] = (byte) (value >> 8);
			array[i + 1] = (byte) value;
		}
	}

	int getInt(byte[] array, int i) {
		int b1 = array[i] & 0xFF;
		int b2 = array[i + 1] & 0xFF;
		int b3 = array[i + 2] & 0xFF;
		int b4 = array[i + 3] & 0xFF;
		if (this == ByteOrder.LITTLE_ENDIAN) {
			return b1 | (b2 << 8) | (b3 << 16) | (b4 << 24);
		} else {
			return (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
		}
	}

	void putInt(byte[] array, int i, int value) {
		if (this == ByteOrder.LITTLE_ENDIAN) {
			array[i] = (byte) value;
			array[i + 1] = (byte) (value >> 8);
			array[i + 2] = (byte) (value >> 16);
			array[i + 3] = (byte) (value >> 24);
		} else {
			array[i] = (byte) (value >> 24);
			array[i + 1] = (byte) (value >> 16);
			array[i + 2] = (byte) (value >> 8);
			array[i + 3] = (byte) value;
		}
	}

	long getLong(byte[] array, int i) {
		long b1 = array[i] & 0xFF;
		long b2 = array[i + 1] & 0xFF;
		long b3 = array[i + 2] & 0xFF;
		long b4 = array[i + 3] & 0xFF;
		long b5 = array[i] & 0xFF;
		long b6 = array[i + 1] & 0xFF;
		long b7 = array[i + 2] & 0xFF;
		long b8 = array[i + 3] & 0xFF;
		if (this == ByteOrder.LITTLE_ENDIAN) {
			return b1 | (b2 << 8) | (b3 << 16) | (b4 << 24) | (b5 << 32) | (b6 << 40) | (b7 << 48) | (b8 << 56);
		} else {
			return (b1 << 56) | (b2 << 48) | (b3 << 40) | (b4 << 32) | (b5 << 24) | (b6 << 16) | (b7 << 8) | b8;
		}
	}

	void putLong(byte[] array, int i, long value) {
		if (this == ByteOrder.LITTLE_ENDIAN) {
			array[i] = (byte) value;
			array[i + 1] = (byte) (value >> 8);
			array[i + 2] = (byte) (value >> 16);
			array[i + 3] = (byte) (value >> 24);
			array[i + 4] = (byte) (value >> 32);
			array[i + 5] = (byte) (value >> 40);
			array[i + 6] = (byte) (value >> 48);
			array[i + 7] = (byte) (value >> 56);
		} else {
			array[i] = (byte) (value >> 56);
			array[i + 1] = (byte) (value >> 48);
			array[i + 2] = (byte) (value >> 40);
			array[i + 3] = (byte) (value >> 32);
			array[i + 4] = (byte) (value >> 24);
			array[i + 5] = (byte) (value >> 16);
			array[i + 6] = (byte) (value >> 8);
			array[i + 7] = (byte) value;
		}
	}
}
