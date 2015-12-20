package java.util;

import com.colbyskeggs.support.CUtil;

public class Arrays {

	private Arrays() {
	}

	public static <T> T[] copyOf(T[] suppressed, int i) {
		T[] duplicate = CUtil.copyOfType(suppressed, i);
		System.arraycopy(suppressed, 0, duplicate, 0, Math.min(i, suppressed.length));
		return duplicate;
	}
}
