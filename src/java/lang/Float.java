package java.lang;

public final class Float extends Number {

    private final float value;

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
}
