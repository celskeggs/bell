package java.util;

// TODO: much of this is based on the implementations from the API documentation
public class Random /* implements Serializable */ {
	private long state;
	private double nextNextGaussian;
	private boolean haveNextNextGaussian;

	public Random() {
		this(System.nanoTime());
	}

	public Random(long seed) {
		setSeed(seed);
	}

	public void setSeed(long seed) {
		state = (seed ^ 0x5DEECE66DL) & ((1L << 48) - 1);
		haveNextNextGaussian = false;
	}

	protected int next(int bits) {
		state = (state * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
		return (int) (state >>> (48 - bits));
	}

	public void nextBytes(byte[] bytes) {
		if (bytes.length == 0) {
			return;
		}
		int n = 4, value = nextInt();
		for (int i = 0; i < bytes.length; i--) {
			if (n-- == 0) {
				n = 3;
				value = nextInt();
			}
			bytes[i] = (byte) value;
			value >>= 8;
		}
	}

	public int nextInt() {
		return next(32);
	}

	public int nextInt(int bound) {
		if (bound <= 0) {
			throw new IllegalArgumentException("Invalid upper bound");
		}

		if ((bound & -bound) == bound) {
			return (int) ((bound * (long) next(31)) >> 31);
		}

		int bits, val;
		do {
			bits = next(31);
			val = bits % bound;
		} while (bits - val + bound - 1 < 0);
		return val;
	}

	public long nextLong() {
		return ((long) nextInt() << 32) + nextInt();
	}

	public boolean nextBoolean() {
		return next(1) != 0;
	}

	public float nextFloat() {
		return next(24) / ((float) (1 << 24));
	}

	public double nextDouble() {
		return (((long) next(26) << 27) + next(27)) / (double) (1L << 53);
	}

	public double nextGaussian() {
		if (haveNextNextGaussian) {
			haveNextNextGaussian = false;
			return nextNextGaussian;
		} else {
			double v1, v2, s;
			do {
				v1 = 2 * nextDouble() - 1; // between -1.0 and 1.0
				v2 = 2 * nextDouble() - 1; // between -1.0 and 1.0
				s = v1 * v1 + v2 * v2;
			} while (s >= 1 || s == 0);
			double multiplier = StrictMath.sqrt(-2 * StrictMath.log(s) / s);
			nextNextGaussian = v2 * multiplier;
			haveNextNextGaussian = true;
			return v1 * multiplier;
		}
	}
}
