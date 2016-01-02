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

	public static boolean equals(Object[] a, Object[] b) {
		if (a == b) {
			return true;
		} else if (a == null || b == null) {
			return false;
		} else if (a.length != b.length) {
			return false;
		} else {
			for (int i = 0; i < a.length; i++) {
				if (!Objects.equals(a[i], b[i])) {
					return false;
				}
			}
			return true;
		}
	}
}
