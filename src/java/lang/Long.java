package java.lang;

public final class Long extends Number {

	public static final int SIZE = 64;
	public static final long MAX_VALUE = 9223372036854775807L;
	public static final long MIN_VALUE = -9223372036854775808L;
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

	public static int bitCount(long i) {
		// taken from http://stackoverflow.com/a/34116264/3369324
		i = i - ((i >>> 1) & 0x5555555555555555L);
		i = (i & 0x3333333333333333L) + ((i >>> 2) & 0x3333333333333333L);
		i = (i + (i >>> 4)) & 0x0f0f0f0f0f0f0f0fL;
		i = i + (i >>> 8);
		i = i + (i >>> 16);
		i = i + (i >>> 32);
		return (int) i & 0x7f;
	}
}
