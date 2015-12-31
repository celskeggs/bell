package java.util;

public class BitSet /* implements Serializable, Clonable */ {

	// each long contains 64 bits: see Long.SIZE
	protected long[] contents;

	// BitSet.valueOf(longs).get(n) == ((longs[n/64] & (1L<<(n%64))) != 0)

	public BitSet() {
		this(128);
	}

	public BitSet(int nbits) {
		contents = new long[nbits / Long.SIZE + 2];
	}

	private BitSet(long[] longs) {
		this.contents = longs;
	}

	public static BitSet valueOf(long[] longs) {
		return new BitSet(Arrays.copyOf(longs, longs.length));
	}

	public static BitSet valueOf(LongBuffer lb) {
		// TODO
		throw new IncompleteImplementationError();
	}

	public static BitSet valueOf(byte[] bytes) {
		long[] longs = new long[(bytes.length + 7) >> 3];
		for (int i = 0; i < bytes.length; i++) {
			longs[i >> 3] |= (bytes[i] & 0xFF) << (8 * (i & 7));
		}
		return valueOf(longs);
	}

	public static BitSet valueOf(ByteBuffer bb) {
		// TODO
		throw new IncompleteImplementationError();
	}

	public byte[] toByteArray() {
		byte[] out = new byte[contents.length * 8];
		for (int i = 0; i < contents.length; i++) {
			long l = contents[i];
			out[(i << 3) | 0] = (byte) l;
			out[(i << 3) | 1] = (byte) (l >> 8);
			out[(i << 3) | 2] = (byte) (l >> 16);
			out[(i << 3) | 3] = (byte) (l >> 24);
			out[(i << 3) | 4] = (byte) (l >> 32);
			out[(i << 3) | 5] = (byte) (l >> 40);
			out[(i << 3) | 6] = (byte) (l >> 48);
			out[(i << 3) | 7] = (byte) (l >> 56);
		}
		return out;
	}

	public long[] toLongArray() {
		return Arrays.copyOf(contents, contents.length);
	}

	public void flip(int bitIndex) {
		if (bitIndex < 0) {
			throw new IndexOutOfBoundsException();
		}
		int cell = (bitIndex >> 6);
		if (cell >= contents.length) {
			set(bitIndex); // would be always false
		} else {
			contents[cell] ^= 1L << (bitIndex & 63);
		}
	}

	public void set(int bitIndex) {
		if (bitIndex < 0) {
			throw new IndexOutOfBoundsException();
		}
		int cell = (bitIndex >> 6);
		if (cell >= contents.length) {
			ensure(cell + 1);
		}
		contents[cell] |= 1L << (bitIndex & 63);
	}

	private void ensure(int minlen) {
		if (contents.length < minlen) {
			contents = Arrays.copyOf(contents, Math.max(contents.length * 2, minlen + 1));
		}
	}

	public void clear(int bitIndex) {
		if (bitIndex < 0) {
			throw new IndexOutOfBoundsException();
		}
		int cell = (bitIndex >> 6);
		if (cell >= contents.length) {
			return; // already certainly false
		}
		contents[cell] &= ~(1L << (bitIndex & 63));
	}

	public void set(int bitIndex, boolean value) {
		if (value) {
			set(bitIndex);
		} else {
			clear(bitIndex);
		}
	}

	public void flip(int fromIndex, int toIndex) {
		if (fromIndex < 0 || toIndex < 0 || fromIndex > toIndex) {
			throw new IndexOutOfBoundsException();
		}
		// ********* THIS CODE HAS THREE COPIES IN THIS FILE *********
		int fromCell = fromIndex >> 6;
		int toCell = toIndex >> 6;
		if (toCell >= contents.length) {
			ensure(toCell + 1);
		}
		if (fromCell == toCell) {
			int count = toIndex - fromIndex;
			if (count != 0) {
				long mask = ((-1L) >>> (64 - count)) << (fromIndex & 63);
				contents[fromCell] ^= mask;
			}
		} else {
			// all the body cells that are entirely included
			for (int cell = fromCell + 1; cell < toCell; cell++) {
				contents[cell] ^= -1L;
			}
			// the first cell
			if ((fromIndex & 63) == 0) {
				contents[fromCell] ^= -1L;
			} else {
				long mask = ((-1L) << (64 - (fromIndex & 63)));
				contents[fromCell] ^= mask;
			}
			// the last cell
			if ((toIndex & 63) != 0) {
				contents[toCell] ^= ((-1L) >>> (64 - (toIndex & 63)));
			}
		}
		// ********* BE CERTAIN TO UPDATE ALL OF THEM *********
	}

	public void set(int fromIndex, int toIndex) {
		if (fromIndex < 0 || toIndex < 0 || fromIndex > toIndex) {
			throw new IndexOutOfBoundsException();
		}
		// ********* THIS CODE HAS THREE COPIES IN THIS FILE *********
		int fromCell = fromIndex >> 6;
		int toCell = toIndex >> 6;
		if (toCell >= contents.length) {
			ensure(toCell + 1);
		}
		if (fromCell == toCell) {
			int count = toIndex - fromIndex;
			if (count != 0) {
				long mask = ((-1L) >>> (64 - count)) << (fromIndex & 63);
				contents[fromCell] |= mask;
			}
		} else {
			// all the body cells that are entirely included
			for (int cell = fromCell + 1; cell < toCell; cell++) {
				contents[cell] = -1L;
			}
			// the first cell
			if ((fromIndex & 63) == 0) {
				contents[fromCell] = -1L;
			} else {
				long mask = ((-1L) << (64 - (fromIndex & 63)));
				contents[fromCell] |= mask;
			}
			// the last cell
			if ((toIndex & 63) != 0) {
				contents[toCell] |= ((-1L) >>> (64 - (toIndex & 63)));
			}
		}
		// ********* BE CERTAIN TO UPDATE ALL OF THEM *********
	}

	public void clear(int fromIndex, int toIndex) {
		if (fromIndex < 0 || toIndex < 0 || fromIndex > toIndex) {
			throw new IndexOutOfBoundsException();
		}
		// ********* THIS CODE HAS THREE COPIES IN THIS FILE *********
		int fromCell = fromIndex >> 6;
		int toCell = toIndex >> 6;
		if (fromCell == toCell) {
			if (fromCell >= contents.length) {
				return;
			}
			int count = toIndex - fromIndex;
			if (count != 0) {
				long mask = ((-1L) >>> (64 - count)) << (fromIndex & 63);
				contents[fromCell] &= ~mask;
			}
		} else {
			fromIndex &= 63;
			toIndex &= 63;
			if (fromCell >= contents.length) {
				// all cells are definitely false
				return;
			}
			if (toCell >= contents.length) {
				// any such cells are definitely false
				toCell = contents.length;
				toIndex = 0; // so that all of the previous cells are handled
								// (up to contents.length, but not including)
			}
			// all the body cells that are entirely included
			for (int cell = fromCell + 1; cell < toCell; cell++) {
				contents[cell] = 0L;
			}
			// the first cell
			if (fromIndex == 0) {
				contents[fromCell] = 0L;
			} else {
				long mask = ((-1L) << (64 - fromIndex));
				contents[fromCell] &= ~mask;
			}
			// the last cell
			if ((toIndex & 63) != 0) {
				contents[toCell] &= ~((-1L) >>> (64 - (toIndex & 63)));
			}
		}
		// ********* BE CERTAIN TO UPDATE ALL OF THEM *********
	}

	public void set(int fromIndex, int toIndex, boolean value) {
		if (value) {
			set(fromIndex, toIndex);
		} else {
			clear(fromIndex, toIndex);
		}
	}

	public void clear() {
		for (int i = 0; i < contents.length; i++) {
			contents[i] = 0L;
		}
	}

	public boolean get(int bitIndex) {
		if (bitIndex < 0) {
			throw new IndexOutOfBoundsException();
		}
		int cell = bitIndex >> 6;
		if (cell >= contents.length) {
			return false;
		}
		return (contents[cell] & (1L << (bitIndex & 63))) != 0;
	}

	public BitSet get(int fromIndex, int toIndex) {
		// TODO: test this code
		if (fromIndex < 0 || toIndex < 0 || fromIndex > toIndex) {
			throw new IndexOutOfBoundsException();
		}
		if ((fromIndex >> 6) >= contents.length) {
			return new BitSet(); // all false!
		}
		if ((toIndex >> 6) >= contents.length) {
			toIndex = contents.length << 6;
		}
		// A last partial cell full of zeros? Unnecessary.
		if ((toIndex & 63) != 0) {
			long mask = ((-1L) >>> (64 - (toIndex & 63)));
			if ((contents[toIndex >> 6] & mask) == 0) {
				toIndex &= ~63;
			}
		}
		// The last cell full of zeros? Unnecessary.
		if ((toIndex & 63) == 0) {
			while (toIndex > fromIndex && contents[(toIndex - 1) >> 6] == 0) {
				toIndex -= 64;
			}
		}
		if (toIndex <= fromIndex) {
			return new BitSet(); // all false!
		}
		if ((fromIndex & 63) == 0) {
			// aligned! this can be faster.
			long[] out = Arrays.copyOfRange(contents, fromIndex >> 6, ((toIndex - 1) >> 6) + 1);
			if ((toIndex & 63) != 0) {
				long mask = ((-1L) >>> (64 - (toIndex & 63)));
				out[toIndex >> 6] &= mask;
			}
			return new BitSet(out);
		} else {
			// unaligned! this is slow no matter what.
			long[] out = new long[(toIndex - fromIndex) >> 6];
			// we'll copy over everything, including overlaps, and then drop the
			// unnecessary stuff
			int baseCell = (fromIndex >> 6);
			int dropFromA = fromIndex & 63;
			int dropFromB = 64 - dropFromA;
			for (int i = 0; i < out.length; i++) {
				int cellA = baseCell + i, cellB = cellA + 1;
				out[i] = (contents[cellA] >>> dropFromA) | (contents[cellB] << dropFromB);
			}
			// now that it's all copied, drop anything extra in the last cell?
			int maxIndex = fromIndex + 64 * out.length;
			int dropBits = maxIndex - toIndex; // [0, 63]
			long keepMask = (-1L) >>> dropBits;
			out[out.length - 1] &= keepMask;
			return new BitSet(out);
		}
	}

	public int nextSetBit(int fromIndex) {
		// *** code mostly duplicated below ***
		if (fromIndex < 0) {
			throw new IndexOutOfBoundsException();
		}
		int fromCell = fromIndex >> 6;
		if (fromCell >= contents.length) {
			return -1;
		}
		if ((contents[fromCell] & (-1L << (fromIndex & 63))) != 0) {
			// found something immediately! let's get closer.
		} else {
			// nothing nearby: skip ahead
			do {
				if (++fromCell >= contents.length) {
					return -1;
				}
			} while (contents[fromCell] == 0);
			// found something! let's get closer
			fromIndex = fromCell << 6;
		}
		long value = contents[fromCell];
		// TODO: optimize this? I think it can be done with binary search.
		for (int i = fromIndex & 63; i < Long.SIZE; i++) {
			if ((value & (1L << i)) != 0) {
				return i + (fromCell << 6);
			}
		}
		// should always succeed! one of those bits is known to be set.
		throw new Error("Unexpected internal state.");
	}

	public int nextClearBit(int fromIndex) {
		// *** code mostly duplicated above ***
		if (fromIndex < 0) {
			throw new IndexOutOfBoundsException();
		}
		int fromCell = fromIndex >> 6;
		if (fromCell >= contents.length) {
			// anything off the end is FALSE
			return fromIndex;
		}
		if (((~contents[fromCell]) & (-1L << (fromIndex & 63))) != 0) {
			// found something immediately! let's get closer.
		} else {
			// nothing nearby: skip ahead
			do {
				if (++fromCell >= contents.length) {
					// anything off the end is FALSE
					return fromCell << 6;
				}
			} while (contents[fromCell] == -1L);
			// found something! let's get closer
			fromIndex = fromCell << 6;
		}
		long value = contents[fromCell];
		// TODO: optimize this? I think it can be done with binary search.
		for (int i = fromIndex & 63; i < Long.SIZE; i++) {
			if ((value & (1L << i)) == 0) {
				return i + (fromCell << 6);
			}
		}
		// should always succeed! one of those bits is known to be unset.
		throw new Error("Unexpected internal state.");
	}

	public int previousSetBit(int fromIndex) {
		// *** duplicated below ***
		if (fromIndex < 0) {
			if (fromIndex == -1) {
				return -1;
			}
			throw new IndexOutOfBoundsException();
		}
		int fromCell = fromIndex >> 6;
		if (fromCell >= contents.length) {
			fromCell = contents.length - 1;
			fromIndex = (fromCell << 6) | 63;
		}
		if ((contents[fromCell] & (-1L >>> (63 - (fromIndex & 63)))) != 0) {
			// found something immediately! let's get closer.
		} else {
			// nothing nearby: skip ahead
			do {
				if (--fromCell < 0) {
					return -1;
				}
			} while (contents[fromCell] == 0);
			// found something! let's get closer
			fromIndex = (fromCell << 6) | 63;
		}
		long value = contents[fromCell];
		// TODO: optimize this? I think it can be done with binary search.
		for (int i = fromIndex & 63; i >= 0; i--) {
			if ((value & (1L << i)) != 0) {
				return i + (fromCell << 6);
			}
		}
		// should always succeed! one of those bits is known to be set.
		throw new Error("Unexpected internal state.");
	}

	public int previousClearBit(int fromIndex) {
		// *** duplicated above ***
		if (fromIndex < 0) {
			if (fromIndex == -1) {
				return -1;
			}
			throw new IndexOutOfBoundsException();
		}
		int fromCell = fromIndex >> 6;
		if (fromCell >= contents.length) {
			return fromIndex;
		}
		if (((~contents[fromCell]) & (-1L >>> (63 - (fromIndex & 63)))) != 0) {
			// found something immediately! let's get closer.
		} else {
			// nothing nearby: skip ahead
			do {
				if (--fromCell < 0) {
					return -1;
				}
			} while (contents[fromCell] == -1L);
			// found something! let's get closer
			fromIndex = (fromCell << 6) | 63;
		}
		long value = contents[fromCell];
		// TODO: optimize this? I think it can be done with binary search.
		for (int i = fromIndex & 63; i >= 0; i--) {
			if ((value & (1L << i)) == 0) {
				return i + (fromCell << 6);
			}
		}
		// should always succeed! one of those bits is known to be unset.
		throw new Error("Unexpected internal state.");
	}

	public int length() {
		return previousSetBit(contents.length << 6) + 1;
	}

	public boolean isEmpty() {
		for (int i = 0; i < contents.length; i++) {
			if (contents[i] != 0) {
				return false;
			}
		}
		return true;
	}

	public boolean intersects(BitSet set) {
		int len = Math.min(contents.length, set.contents.length);
		for (int i = 0; i < len; i++) {
			if ((contents[i] & set.contents[i]) != 0) {
				return true;
			}
		}
		return false;
	}

	public int cardinality() {
		int count = 0;
		for (long v : contents) {
			count += Long.bitCount(v);
		}
		return count;
	}

	public void and(BitSet set) {
		int i;
		if (contents.length <= set.contents.length) {
			for (i = 0; i < contents.length; i++) {
				contents[i] &= set.contents[i];
			}
		} else {
			for (i = 0; i < set.contents.length; i++) {
				contents[i] &= set.contents[i];
			}
			for (; i < contents.length; i++) {
				contents[i] = 0; // because other set is all false
			}
		}
	}

	public void or(BitSet set) {
		int i;
		if (contents.length <= set.contents.length) {
			// make sure we're long enough for everything they'll throw at us
			for (i = set.contents.length - 1; i >= contents.length; i--) {
				if (set.contents[i] != 0) {
					ensure(i + 1);
					break;
				}
			}
			for (i = 0; i < contents.length; i++) {
				contents[i] |= set.contents[i];
			}
		} else {
			for (i = 0; i < set.contents.length; i++) {
				contents[i] |= set.contents[i];
			}
		}
	}

	public void xor(BitSet set) {
		int i;
		if (contents.length <= set.contents.length) {
			// make sure we're long enough for everything they'll throw at us
			for (i = set.contents.length - 1; i >= contents.length; i--) {
				if (set.contents[i] != 0) {
					ensure(i + 1);
					break;
				}
			}
			for (i = 0; i < contents.length; i++) {
				contents[i] ^= set.contents[i];
			}
		} else {
			for (i = 0; i < set.contents.length; i++) {
				contents[i] ^= set.contents[i];
			}
		}
	}

	public void andNot(BitSet set) {
		int i;
		if (contents.length <= set.contents.length) {
			for (i = 0; i < contents.length; i++) {
				contents[i] &= ~set.contents[i];
			}
		} else {
			for (i = 0; i < set.contents.length; i++) {
				contents[i] &= ~set.contents[i];
			}
			// anything else won't get touched: ~false is true, which doesn't
			// affect anything during anding.
		}
	}

	public int hashCode() {
		long h = 1234;
		for (int i = contents.length; --i >= 0;) {
			h ^= contents[i] * (i + 1);
		}
		return (int) ((h >> 32) ^ h);
	}

	public int size() {
		return contents.length << 6;
	}

	public boolean equals(Object obj) {
		if (obj instanceof BitSet) {
			BitSet b = (BitSet) obj;
			if (contents.length <= b.contents.length) {
				for (int i = 0; i < contents.length; i++) {
					if (contents[i] != b.contents[i]) {
						return false;
					}
				}
				for (int i = contents.length; i < b.contents.length; i++) {
					if (b.contents[i] != 0) {
						return false;
					}
				}
			} else {
				for (int i = 0; i < b.contents.length; i++) {
					if (contents[i] != b.contents[i]) {
						return false;
					}
				}
				for (int i = b.contents.length; i < contents.length; i++) {
					if (contents[i] != 0) {
						return false;
					}
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public Object clone() {
		throw new IncompleteImplementationError();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("{");
		for (int i = 0; i < contents.length; i++) {
			long l = contents[i];
			for (int b = 0; b < Long.SIZE; b++) {
				if ((l & (1L << b)) != 0) {
					sb.append(((i << 6) | b)).append(", ");
				}
			}
		}
		if (sb.length() >= 3) {
			sb.setLength(sb.length() - 2);
		}
		return sb.append("}").toString();
	}
}
