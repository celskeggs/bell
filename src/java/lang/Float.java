package java.lang;

import com.celskeggs.support.IncompleteImplementationError;

import vm.VMClass;

public final class Float extends Number {

	public static final Class<?> TYPE = VMClass.FLOAT.getRealClass();

	private final float value;

	public static final float NaN = Float.intBitsToFloat(0x7fc00000);

	public Float(float value) {
		this.value = value;
	}

	public int intValue() {
		return (int) value;
	}

	public String toString() {
		return toString(value);
	}

	public int hashCode() {
		return Float.floatToIntBits(value);
	}

	public static int floatToIntBits(float value2) {
		throw new IncompleteImplementationError();
	}

	public static float intBitsToFloat(int readInt) {
		throw new IncompleteImplementationError();
	}

	public boolean equals(Object o) {
		return (o instanceof Float) && (((Float) o).value == value);
	}

	public double doubleValue() {
		return value;
	}

	public float floatValue() {
		return value;
	}

	public long longValue() {
		return (long) value;
	}

	public static String toString(float f) {
		throw new IncompleteImplementationError();
	}

	public static boolean isNaN(float f) {
		return f != f;
	}

	public static int compare(float x, float y) {
		if (Float.isNaN(x)) {
			if (Float.isNaN(y)) {
				return 0;
			} else {
				return 1;
			}
		} else if (Float.isNaN(y)) {
			return -1;
		} else {
			if (x < y) {
				return -1;
			} else if (x > y) {
				return 1;
			} else {
				if (x == 0) {
					if (Float.floatToIntBits(x) == Float.floatToIntBits(0.0f)) {
						if (Float.floatToIntBits(y) == Float.floatToIntBits(0.0f)) {
							return 0;
						} else {
							return 1;
						}
					} else {
						if (Float.floatToIntBits(y) == Float.floatToIntBits(0.0f)) {
							return -1;
						} else {
							return 0;
						}
					}
				}
				return 0;
			}
		}
	}
}
