package java.util;

import org.apache.bcel.generic.LocalVariableGen;

import com.celskeggs.bell.support.CUtil;
import com.celskeggs.bell.support.IncompleteImplementationError;

public class Arrays {

	private Arrays() {
	}

	public static <T> T[] copyOf(T[] original, int newLength) {
		T[] created = CUtil.copyOfType(original, newLength);
		System.arraycopy(original, 0, created, 0, Math.min(newLength, original.length));
		return created;
	}

	public static long[] copyOf(long[] original, int newLength) {
		long[] created = new long[newLength];
		System.arraycopy(original, 0, created, 0, Math.min(newLength, original.length));
		return created;
	}

	public static long[] copyOfRange(long[] original, int from, int to) {
		if (from > to) {
			throw new IllegalArgumentException();
		}
		long[] created = new long[to - from];
		System.arraycopy(original, from, created, 0, created.length);
		return created;
	}

	public static byte[] copyOf(byte[] original, int newLength) {
		byte[] created = new byte[newLength];
		System.arraycopy(original, 0, created, 0, Math.min(newLength, original.length));
		return created;
	}

	public static char[] copyOf(char[] original, int newLength) {
		char[] created = new char[newLength];
		System.arraycopy(original, 0, created, 0, Math.min(newLength, original.length));
		return created;
	}

	public static boolean equals(Object[] a, Object[] b) {
		if (a == b) {
			return true;
		} else if (a == null || b == null) {
			return false;
		} else if (a.length != b.length) {
			return false;
		} else {
			for (int i = 0; i < a.length; i++) {
				if (!Objects.equals(a[i], b[i])) {
					return false;
				}
			}
			return true;
		}
	}

	public static void fill(byte[] data, int start, int end, byte b) {
		// TODO: make this faster
		for (int i = start; i < end; i++) {
			data[i] = b;
		}
	}

    public static <E> List<E> asList(final E[] a) {
        return new AbstractList<E>() {
            @Override public E get(int index) {
                return a[index];
            }

            @Override public int size() {
                return a.length;
            }
        };
    }

    public static String toString(int[] a) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < a.length; i++) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(a[i]);
        }
        return sb.append("]").toString();
    }
    
    private static <T> void merge(T[] arr, int start, int boundary, int end, T[] scratch, Comparator<? super T> comparator) {
        System.arraycopy(arr, start, scratch, start, end - start);
        int sourceA = start, sourceB = boundary;
        int output = start;
        while (sourceA < boundary && sourceB < end) {
            if (comparator.compare(scratch[sourceA], scratch[sourceB]) <= 0) {
                // use left
                arr[output++] = scratch[sourceA++];
            } else {
                // use right
                arr[output++] = scratch[sourceB++];
            }
        }
        while (sourceA < boundary) {
            arr[output++] = scratch[sourceA++];
        }
        while (sourceB < end) {
            arr[output++] = scratch[sourceB++];
        }
        if (output != end) {
            throw new RuntimeException("internal sort error");
        }
    }
    
    private static <T> void sort(T[] arr, int start, int end, T[] scratch, Comparator<? super T> comparator) {
        if (end - start <= 1) {
            return;
        }
        int boundary = start / 2 + end / 2 + (start % 2 + end % 2) / 2;
        sort(arr, start, boundary, scratch, comparator);
        sort(arr, boundary, end, scratch, comparator);
        merge(arr, start, boundary, end, scratch, comparator);
    }

    // TODO: ensure this is stable
    public static <T> void sort(T[] arr, Comparator<? super T> comparator) {
        T[] scratch = (T[]) new Object[arr.length];
        sort(arr, 0, arr.length, scratch, comparator);
    }
}
