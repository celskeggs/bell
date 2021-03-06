package java.lang;

public final class StringBuilder {

	private char[] data;
	private int length;

	public StringBuilder() {
		data = new char[16];
		length = 0;
	}

	public StringBuilder(int length) {
		if (length < 0) {
			throw new NegativeArraySizeException();
		}
		data = new char[length];
		this.length = 0;
	}

	public StringBuilder(String base) {
		int ln = base.length();
		data = new char[ln + 16];
		base.getChars(0, ln, data, 0);
	}

	public StringBuilder(CharSequence base) {
		this(base.toString());
	}

	public int length() {
		return length;
	}

	public int capacity() {
		return data.length;
	}

	public void ensureCapacity(int minimumCapacity) {
		if (data.length < minimumCapacity) {
			if (minimumCapacity < data.length * 2) {
				minimumCapacity = data.length * 2;
			}
			char[] ndata = new char[minimumCapacity];
			System.arraycopy(data, 0, ndata, 0, length);
			data = ndata;
		}
	}

	public void trimToSize() {
		if (data.length > length) {
			char[] ndata = new char[length];
			System.arraycopy(data, 0, ndata, 0, length);
			data = ndata;
		}
	}

	public void setLength(int newLength) {
		if (newLength < 0) {
			throw new IndexOutOfBoundsException();
		}
		for (int i = length; i < newLength; i++) {
			data[i] = '\u0000';
		}
		length = newLength;
	}

	public char charAt(int index) {
		if (index < 0 || index >= length) {
			throw new IndexOutOfBoundsException();
		}
		return data[index];
	}

	// public int codePointAt(int index) - TODO implement code point support

	// public int codePointBefore(int index)

	// public int codePointCount(int beginIndex, int endIndex)

	// public int offsetByCodePoints(int index, int codePointOffset)

	public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
		if (srcBegin < 0 || dstBegin < 0 || srcBegin > srcEnd || srcEnd > length
				|| dstBegin + srcEnd - srcBegin > dst.length) {
			throw new IndexOutOfBoundsException();
		}
		for (int i = srcBegin, j = dstBegin; i < srcEnd; i++, j++) {
			dst[j] = data[i];
		}
	}

	public void setCharAt(int index, char ch) {
		if (index < 0 || index >= length) {
			throw new IndexOutOfBoundsException();
		}
		data[index] = ch;
	}

	public StringBuilder append(Object obj) {
		return append(String.valueOf(obj));
	}

	public StringBuilder append(String str) {
		if (str == null) {
			str = "null";
		}
		int ln = str.length();
		ensureCapacity(length + ln);
		str.getChars(0, ln, data, length);
		length += ln;
		return this;
	}

	public StringBuilder append(StringBuffer str) {
		if (str == null) {
			return append("null");
		}
		int ln = str.length();
		ensureCapacity(length + ln);
		str.getChars(0, ln, data, length);
		length += ln;
		return this;
	}

	public StringBuilder append(CharSequence s) {
		if (s == null) {
			s = "null";
		}
		return append(s.toString());
	}

	public StringBuilder append(CharSequence s, int start, int end) {
		if (s == null) {
			s = "null";
		}
		return append(s.subSequence(start, end).toString());
	}

	public StringBuilder append(char[] str) {
		return append(String.valueOf(str));
	}

	public StringBuilder append(char[] str, int off, int len) {
		return append(String.valueOf(str, off, len));
	}

	public StringBuilder append(boolean b) {
		return append(String.valueOf(b));
	}

	public StringBuilder append(char c) {
		return append(String.valueOf(c));
	}

	public StringBuilder append(int i) {
		return append(String.valueOf(i));
	}

	// public StringBuilder appendCodePoint(int codePoint)

	public StringBuilder append(long l) {
		return append(String.valueOf(l));
	}

	public StringBuilder append(float f) {
		return append(String.valueOf(f));
	}

	public StringBuilder append(double d) {
		return append(String.valueOf(d));
	}

	public StringBuilder delete(int start, int end) {
		// TODO: Check that this method works
		if (start < 0 || start > length || start > end) {
			throw new StringIndexOutOfBoundsException();
		}
		if (end >= length) {
			// remove all
			length = start;
			return this;
		}
		int range = end - start;
		if (range == 0) {
			return this;
		}
		length -= range;
		for (int i = start; i < length; i++) {
			data[i] = data[i + range];
		}
		return this;
	}

	public StringBuilder deleteCharAt(int index) {
		if (index < 0 || index >= length) {
			throw new StringIndexOutOfBoundsException();
		}
		length -= 1;
		for (int i = index; i < length; i++) {
			data[i] = data[i + 1];
		}
		return this;
	}

	public StringBuilder replace(int start, int end, String str) {
		return delete(start, end).insert(start, str);
	}

	public String substring(int start) {
		if (start < 0 || start > length) {
			throw new StringIndexOutOfBoundsException();
		}
		if (start == length) {
			return "";
		} else {
			return new String(data, start, length - start);
		}
	}

	public CharSequence subSequence(int start, int end) {
		return substring(start, end);
	}

	public String substring(int start, int end) {
		if (start < 0 || end < 0 || start > length || end > length || start > end) {
			throw new StringIndexOutOfBoundsException();
		}
		if (start == end) {
			return "";
		}
		return new String(data, start, end);
	}

	public StringBuilder insert(int index, char[] str, int offset, int count) {
		return insert(index, String.valueOf(str, offset, count));
	}

	public StringBuilder insert(int index, Object o) {
		return insert(index, String.valueOf(o));
	}

	public StringBuilder insert(int index, String str) {
		if (index < 0 || index > length) {
			throw new StringIndexOutOfBoundsException();
		}
		if (str == null) {
			str = "null";
		}
		int ln = str.length();
		ensureCapacity(length + ln);
		for (int i = length; i < length + ln; i++) {
			data[i] = data[i - ln];
		}
		str.getChars(0, ln, data, length - ln);
		length += ln;
		return this;
	}

	public StringBuilder insert(int index, char[] o) {
		return insert(index, String.valueOf(o));
	}

	public StringBuilder insert(int index, CharSequence s) {
		return insert(index, s.toString());
	}

	public StringBuilder insert(int index, CharSequence s, int start, int end) {
		return insert(index, s.subSequence(start, end).toString());
	}

	public StringBuilder insert(int index, boolean o) {
		return insert(index, String.valueOf(o));
	}

	public StringBuilder insert(int index, char c) {
		return insert(index, String.valueOf(c));
	}

	public StringBuilder insert(int index, int i) {
		return insert(index, String.valueOf(i));
	}

	public StringBuilder insert(int index, long l) {
		return insert(index, String.valueOf(l));
	}

	public StringBuilder insert(int index, float f) {
		return insert(index, String.valueOf(f));
	}

	public StringBuilder insert(int index, double d) {
		return insert(index, String.valueOf(d));
	}

	public int indexOf(String str) {
		return indexOf(str, 0);
	}

	public int indexOf(String str, int fromIndex) {
		char initial = str.charAt(0);
		for (int i = fromIndex; i <= length - str.length(); i++) {
			if (initial == data[i]) {
				if (str.regionMatches(false, i, str, 0, str.length())) {
					return i;
				}
			}
		}
		return -1;
	}

	public int lastIndexOf(String str) {
		return lastIndexOf(str, str.length());
	}

	public int lastIndexOf(String str, int fromIndex) {
		char initial = str.charAt(0);
		for (int i = length - str.length(); i >= fromIndex; i--) {
			if (initial == data[i]) {
				if (str.regionMatches(false, i, str, 0, str.length())) {
					return i;
				}
			}
		}
		return -1;
	}

	public StringBuilder reverse() {
		for (int i = 0; i < length / 2; i++) {
			char c = data[i];
			int oi = length - i - 1;
			data[i] = data[oi];
			data[oi] = c;
		}
		// TODO: go back and fix surrogate pairs
		return this;
	}

	public String toString() {
		return new String(this);
	}
}
