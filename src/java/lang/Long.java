package java.lang;

public final class Long extends Number {

	private final long value;

	public Long(long value) {
		this.value = value;
	}

	public int intValue() {
		return (int) value;
	}

	private static char[] digits = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();

	public static String toString(long i, int radix) {
		if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX) {
			radix = 10;
		}
		boolean negative;
		if (i == 0) {
			return "0";
		} else if (i < 0) {
			negative = true;
			i = -i;
		} else {
			negative = false;
		}
		char[] out = new char[28]; // TODO: find a better number than 28
		int ci = 28;
		while (i != 0) {
			int mod = (int) (i % radix);
			i = i / radix;
			out[--ci] = digits[mod];
		}
		if (negative) {
			out[--ci] = '-';
		}
		return new String(out, ci, 28 - ci, true);
	}

	public static String toString(long l) {
		return toString(l, 10);
	}

	public String toString() {
		return toString(value);
	}

	public int hashCode() {
		return (int) (value ^ (value >> 32));
	}

	public boolean equals(Object o) {
		return (o instanceof Long) && (((Long) o).value == value);
	}

	public double doubleValue() {
		return value;
	}

	public float floatValue() {
		return value;
	}

	public long longValue() {
		return value;
	}
}
