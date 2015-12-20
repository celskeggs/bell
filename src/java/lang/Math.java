package java.lang;

public class Math {
	private Math() {
	}

	public static int max(int a, int b) {
		return a > b ? a : b;
	}

	public static int min(int a, int b) {
		return a < b ? a : b;
	}

	public static int abs(int x) {
		return x < 0 ? -x : x;
	}
}
