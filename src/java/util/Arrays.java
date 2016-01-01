package java.util;

import com.colbyskeggs.support.CUtil;

public class Arrays {

	private Arrays() {
	}

	public static <T> T[] copyOf(T[] original, int newLength) {
		T[] created = CUtil.copyOfType(original, newLength);
		System.arraycopy(original, 0, created, 0, Math.min(newLength, original.length));
		return created;
	}

	public static long[] copyOf(long[] original, int newLength) {
		long[] created = new long[newLength];
		System.arraycopy(original, 0, created, 0, Math.min(newLength, original.length));
		return created;
	}

	public static long[] copyOfRange(long[] original, int from, int to) {
		if (from > to) {
			throw new IllegalArgumentException();
		}
		long[] created = new long[to - from];
		System.arraycopy(original, from, created, 0, created.length);
		return created;
	}

	public static byte[] copyOf(byte[] original, int newLength) {
		byte[] created = new byte[newLength];
		System.arraycopy(original, 0, created, 0, Math.min(newLength, original.length));
		return created;
	}

	public static char[] copyOf(char[] original, int newLength) {
		char[] created = new char[newLength];
		System.arraycopy(original, 0, created, 0, Math.min(newLength, original.length));
		return created;
	}
}
