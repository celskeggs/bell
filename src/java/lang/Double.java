package java.lang;

import com.celskeggs.bell.support.IncompleteImplementationError;
import com.celskeggs.bell.vm.VMClass;

public final class Double extends Number {

	public static final Class<?> TYPE = VMClass.DOUBLE.getRealClass();

	private final double value;

	public Double(double value) {
		this.value = value;
	}

	public int intValue() {
		return (int) value;
	}

	public String toString() {
		return toString(value);
	}

	public int hashCode() {
		long l = Double.doubleToLongBits(value);
		return ((int) (l >> 32)) ^ (int) l;
	}

	public static long doubleToLongBits(double value) {
		throw new IncompleteImplementationError();
	}

	public static double longBitsToDouble(long readLong) {
		throw new IncompleteImplementationError();
	}

	public boolean equals(Object o) {
		return (o instanceof Double) && (((Double) o).value == value);
	}

	public double doubleValue() {
		return value;
	}

	public float floatValue() {
		return (float) value;
	}

	public long longValue() {
		return (long) value;
	}

	public static String toString(double d) {
		throw new IncompleteImplementationError();
	}

	public static boolean isNaN(double y) {
		return y != y;
	}
}
