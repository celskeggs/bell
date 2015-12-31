package java.nio;

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
}
