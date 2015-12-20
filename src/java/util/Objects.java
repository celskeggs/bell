package java.util;

public class Objects {
	private Objects() {
	}

	public static boolean equals(Object a, Object b) {
		if (a == null) {
			return b == null;
		} else {
			return b != null && a.equals(b);
		}
	}

	public static int hashCode(Object o) {
		return o == null ? 0 : o.hashCode();
	}

	public static String toString(Object o) {
		return o == null ? "null" : o.toString();
	}
}
