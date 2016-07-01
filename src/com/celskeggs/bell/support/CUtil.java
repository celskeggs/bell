package com.celskeggs.bell.support;

import java.lang.reflect.Array;
import java.util.Enumeration;
import java.util.NoSuchElementException;

public class CUtil {

	public static <T> T[] copyOfType(T[] suppressed, int i) {
		return (T[]) Array.newInstance(suppressed.getClass().getComponentType(), i);
	}

	public static <T> Enumeration<T> emptyEnumeration() {
		return new Enumeration<T>() {
			public boolean hasMoreElements() {
				return false;
			}

			public T nextElement() {
				throw new NoSuchElementException();
			}
		};
	}

	public static <T> Enumeration<T> concatEnumerations(final Enumeration<T> a, final Enumeration<T> b) {
		if (!a.hasMoreElements()) {
			return b;
		} else if (!b.hasMoreElements()) {
			return a;
		}
		return new Enumeration<T>() {
			public boolean hasMoreElements() {
				return a.hasMoreElements() || b.hasMoreElements();
			}

			public T nextElement() {
				return a.hasMoreElements() ? a.nextElement() : b.nextElement();
			}
		};
	}

	public static String[] splitFixed(String substrate, char delimiter) {
		// TODO: test that this actually works
		int count = 0;
		for (int i = 0; i < substrate.length(); i++) {
			if (delimiter == substrate.charAt(i)) {
				count++;
			}
		}
		String[] parts = new String[count + 1];
		int last = 0;
		for (int i = 0; i < count; i++) {
			int next = substrate.indexOf(delimiter, last);
			parts[i] = substrate.substring(last, next);
			last = next + 1;
		}
		parts[count] = substrate.substring(last);
		return parts;
	}

	public static void reverseArray(Object[] arr, int length) {
		for (int i = 0; i < length / 2; i++) {
			Object a = arr[i];
			arr[i] = arr[length - i - 1];
			arr[length - i - 1] = a;
		}
	}
}
