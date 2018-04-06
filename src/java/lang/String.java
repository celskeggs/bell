package java.lang;

import java.io.UnsupportedEncodingException;

import com.celskeggs.bell.support.IncompleteImplementationError;
import com.celskeggs.bell.vm.CharacterCoder;

public final class String implements CharSequence {

	final char[] data;
	private int hashCode;

	public String() {
		data = null;
	}

	public String(String value) {
		data = value.data;
	}

	public String(char[] value) {
		data = new char[value.length];
		System.arraycopy(value, 0, data, 0, value.length);
	}

	public String(char[] value, int offset, int count) {
		data = new char[count];
		System.arraycopy(value, offset, data, 0, count);
	}

	String(char[] value, boolean directcopymarker) {
		data = value;
	}

	public String(byte[] bytes, int offset, int length, String encoding) throws UnsupportedEncodingException {
		data = CharacterCoder.decode(bytes, offset, length, encoding);
	}

	public String(byte[] bytes, String encoding) throws UnsupportedEncodingException {
		this(bytes, 0, bytes.length, encoding);
	}

	public String(byte[] bytes, int offset, int length) {
		try {
			data = CharacterCoder.decode(bytes, offset, length, CharacterCoder.DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException ex) {
			throw new VirtualMachineError("Default encoding is not supported!");
		}
	}

	public String(byte[] bytes) {
		this(bytes, 0, bytes.length);
	}

	public String(StringBuffer buf) {
		synchronized (buf) {
			int length = buf.length();
			data = new char[length];
			buf.getChars(0, length, data, 0);
		}
	}

	public String(StringBuilder buf) {
		int length = buf.length();
		data = new char[length];
		buf.getChars(0, length, data, 0);
	}

	public int length() {
		return data.length;
	}

	public char charAt(int i) {
		if (i < 0 || i >= data.length) {
			throw new IndexOutOfBoundsException();
		}
		return data[i];
	}

	public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
		if (srcBegin < 0 || srcBegin > srcEnd || srcEnd > data.length || dstBegin < 0
				|| dstBegin + srcEnd - srcBegin > dst.length) {
			throw new IndexOutOfBoundsException();
		}
		for (int i = srcBegin, j = dstBegin; i < srcEnd; i++, j++) {
			dst[j] = data[i];
		}
	}

	public byte[] getBytes(String encoding) throws UnsupportedEncodingException {
		return CharacterCoder.encode(data, 0, data.length, encoding);
	}

	public byte[] getBytes() {
		return CharacterCoder.encodeDefault(data, 0, data.length);
	}

	public boolean equals(Object o) {
		if (o instanceof String) {
			if (this == o) {
				return true;
			} else {
				String str = (String) o;
				if (data.length != str.data.length) {
					return false;
				}
				for (int i = 0; i < data.length; i++) {
					if (str.data[i] != data[i]) {
						return false;
					}
				}
				return true;
			}
		} else {
			return false;
		}
	}

	public boolean equalsIgnoreCase(String o) {
		if (this == o) {
			return true;
		} else {
			String str = (String) o;
			if (data.length != str.data.length) {
				return false;
			}
			for (int i = 0; i < data.length; i++) {
				char a = str.data[i];
				char b = data[i];
				// TODO: review this algorithm
				if (a != b && Character.toUpperCase(a) != Character.toUpperCase(b)
						&& Character.toLowerCase(a) != Character.toLowerCase(b)) {
					return false;
				}
			}
			return true;
		}
	}

	public int compareTo(String other) {
		int mlen = Math.min(data.length, other.data.length);
		for (int i = 0; i < mlen; i++) {
			char thisC = data[i];
			char otherC = other.data[i];
			if (thisC != otherC) {
				return thisC - otherC;
			}
		}
		return data.length - other.data.length;
	}

	public boolean regionMatches(boolean ignoreCase, int toffset, String other, int ooffset, int len) {
		if (toffset < 0 || ooffset < 0 || toffset + len > data.length || ooffset + len > other.data.length) {
			return false;
		}
		for (int i = 0; i < len; i++) {
			char thisC = data[i + toffset];
			char otherC = other.data[i + ooffset];
			if (ignoreCase ? (Character.toUpperCase(thisC) != Character.toUpperCase(otherC)
					&& Character.toLowerCase(thisC) != Character.toLowerCase(otherC)) : (thisC != otherC)) {
				return false;
			}
		}
		return true;
	}

	public boolean startsWith(String prefix, int toffset) {
		if (toffset < 0 || toffset > data.length) {
			// TODO: what if prefix is empty?
			return false;
		} else {
			if (prefix.data.length > data.length - toffset) {
				return false;
			}
			return regionMatches(false, toffset, prefix, 0, prefix.data.length);
		}
	}

	public boolean startsWith(String prefix) {
		if (prefix.data.length > data.length) {
			return false;
		}
		return regionMatches(false, 0, prefix, 0, prefix.data.length);
	}

	public boolean endsWith(String suffix) {
		if (suffix.data.length > data.length) {
			return false;
		}
		return regionMatches(false, data.length - suffix.data.length, suffix, 0, suffix.data.length);
	}

	public int hashCode() {
		if (hashCode == 0) { // TODO: What if the hashCode _is_ 0?
			computeHashCode();
		}
		return hashCode;
	}

	private void computeHashCode() {
		int exp = 1;
		int digest = 0;
		for (int i = data.length - 1; i >= 0; i--) {
			digest += data[i] * exp;
			exp *= 31;
		}
		hashCode = digest;
	}

	public int indexOf(int ch) {
		return indexOf(ch, 0);
	}

	public int indexOf(int ch, int fromIndex) {
		if (fromIndex < 0) {
			fromIndex = 0;
		}
		for (int i = fromIndex; i < data.length; i++) {
			if (data[i] == ch) {
				return i;
			}
		}
		return -1;
	}

	public int lastIndexOf(int ch) {
		return lastIndexOf(ch, data.length - 1);
	}

	public int lastIndexOf(int ch, int fromIndex) {
		if (fromIndex >= data.length) {
			fromIndex = data.length - 1;
		}
		for (int i = fromIndex; i >= 0; i--) {
			if (data[i] == ch) {
				return i;
			}
		}
		return -1;
	}

	public int indexOf(String str) {
		return indexOf(str, 0);
	}

	public int indexOf(String str, int fromIndex) {
		if (fromIndex < 0) {
			fromIndex = 0;
		}
		for (int i = fromIndex; i < data.length; i++) {
			if (startsWith(str, i)) {
				return i;
			}
		}
		return -1;
	}

    public boolean contains(String str) {
        return indexOf(str) != -1;
    }

	public int lastIndexOf(String str) {
		return lastIndexOf(str, data.length - str.length());
	}

	public int lastIndexOf(String str, int fromIndex) {
		if (fromIndex >= data.length) {
			fromIndex = data.length - 1;
		}
		for (int i = fromIndex; i >= 0; i--) {
			if (startsWith(str, i)) {
				return i;
			}
		}
		return -1;
	}

	public String substring(int begin) {
		if (begin < 0 || begin > data.length) {
			throw new IndexOutOfBoundsException();
		} else if (begin == 0) {
			return this;
		}
		return new String(data, begin, data.length - begin);
	}

	public String substring(int begin, int end) {
		if (begin < 0 || end > data.length || begin > end) {
			throw new IndexOutOfBoundsException();
		} else if (begin == 0 && end == data.length) {
			return this;
		}
		return new String(data, begin, end - begin);
	}

	public String concat(String str) {
		if (str.data.length == 0) {
			return this;
		} else if (data.length == 0) {
			return str;
		}
		char[] out = new char[data.length + str.data.length];
		System.arraycopy(data, 0, out, 0, data.length);
		System.arraycopy(str.data, 0, out, data.length, str.data.length);
		return new String(out, true);
	}

	public String replace(char old, char nchar) {
		if (indexOf(old) == -1) {
			return this;
		} else {
			char[] narr = new char[data.length];
			for (int i = 0; i < data.length; i++) {
				char c = data[i];
				narr[i] = c == old ? nchar : c;
			}
			return new String(narr, true);
		}
	}

    public String replace(CharSequence targetCS, CharSequence replacementCS) {
        String target = targetCS.toString();
        String replacement = replacementCS.toString();
        StringBuilder result = new StringBuilder(this);
        for (int i = 0, j = 0; i < this.length();) {
            if (this.startsWith(target, i)) {
                result.replace(j, j + target.length(), replacement);
                i += target.length();
                j += replacement.length();
            } else {
                i++;
                j++;
            }
        }
        return result.toString();
    }

	public String toLowerCase() {
		boolean found = false;
		for (int i = 0; i < data.length; i++) {
			char c = data[i];
			if (Character.toLowerCase(c) != c) {
				found = true;
				break;
			}
		}
		if (!found) {
			return this;
		}
		char[] narr = new char[data.length];
		for (int i = 0; i < data.length; i++) {
			narr[i] = Character.toLowerCase(data[i]);
		}
		return new String(narr, true);
	}

	public String toUpperCase() {
		boolean found = false;
		for (int i = 0; i < data.length; i++) {
			char c = data[i];
			if (Character.toUpperCase(c) != c) {
				found = true;
				break;
			}
		}
		if (!found) {
			return this;
		}
		char[] narr = new char[data.length];
		for (int i = 0; i < data.length; i++) {
			narr[i] = Character.toUpperCase(data[i]);
		}
		return new String(narr, true);
	}

	public String trim() {
		int start = 0;
		int end = data.length;
		while (data[start] <= ' ') {
			start++;
			if (start == end) {
				return "";
			}
		}
		while (data[--end] <= ' ')
			;
		return substring(start, end + 1);
	}

	public String toString() {
		return this;
	}

	public char[] toCharArray() {
		char[] out = new char[data.length];
		System.arraycopy(data, 0, out, 0, data.length);
		return out;
	}

	public static String valueOf(Object o) {
		return o == null ? "null" : o.toString();
	}

	public static String valueOf(char[] data) {
		return data.length == 0 ? "" : new String(data);
	}

	public static String valueOf(char[] data, int offset, int count) {
		return count == 0 ? "" : new String(data, offset, count);
	}

	public static String valueOf(boolean b) {
		return b ? "true" : "false";
	}

	public static String valueOf(char c) {
		return new String(new char[] { c }, true);
	}

	public static String valueOf(int i) {
		return Integer.toString(i);
	}

	public static String valueOf(long l) {
		return Long.toString(l);
	}

	public static String valueOf(float f) {
		return Float.toString(f);
	}

	public static String valueOf(double d) {
		return Double.toString(d);
	}

	public native String intern();

	public CharSequence subSequence(int start, int end) {
		return this.substring(start, end);
	}

	public boolean isEmpty() {
		return data.length == 0;
	}

	public static String format(String format, Object... args) {
		throw new IncompleteImplementationError();
	}
}
