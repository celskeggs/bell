package java.lang;

import vm.VMClass;

public final class Character {

	public static final Class TYPE = VMClass.CHAR.getRealClass();

	public static final int MIN_RADIX = 2;
	public static final int MAX_RADIX = 36;
	public static final char MIN_VALUE = '\u0000';
	public static final char MAX_VALUE = '\uffff';
	private final char value;

	public Character(char value) {
		this.value = value;
	}

	public char charValue() {
		return value;
	}

	public int hashCode() {
		return value;
	}

	public boolean equals(Object o) {
		return (o instanceof Character) && (((Character) o).value == value);
	}

	public String toString() {
		return new String(new char[] { value }, true);
	}

	public static boolean isLowerCase(char ch) {
		return (ch >= 'a' && ch <= 'z') || (ch >= '\u00DF' && ch <= '\u00FF' && ch != '\u00F7');
	}

	public static boolean isUpperCase(char ch) {
		return (ch >= 'A' && ch <= 'Z') || (ch >= '\u00C0' && ch <= '\u00DE' && ch != '\u00D7');
	}

	// TODO: Check the implementations of these methods
	public static boolean isDigit(char ch) {
		return (ch >= '0' && ch <= '9');
	}

	public static char toLowerCase(char ch) {
		if ((ch >= 'A' && ch <= 'Z') || (ch >= '\u00C0' && ch <= '\u00DE' && ch != '\u00D7')) {
			return (char) (ch + 32);
		} else {
			return ch;
		}
	}

	public static char toUpperCase(char ch) {
		if ((ch >= 'a' && ch <= 'z') || (ch >= '\u00E0' && ch <= '\u00FE' && ch != '\u00F7')) {
			return (char) (ch - 32);
		} else {
			return ch;
		}
	}

	public static int digit(char c, int radix) {
		if (radix < MIN_RADIX || radix > MAX_RADIX) {
			return -1;
		}
		int out;
		if (c >= '0' && c <= '9') {
			out = c - '0';
		} else if (c >= 'a' && c <= 'z') {
			out = c - 'a' + 10;
		} else if (c >= 'A' && c <= 'Z') {
			out = c - 'A' + 10;
		} else {
			return -1;
		}
		return out < radix ? out : -1;
	}

	public static boolean isLetter(char ch) {
		return isUpperCase(ch) || isLowerCase(ch);
	}

	public static boolean isIdentifierIgnorable(char ch) {
		return (ch >= 0 && ch <= 8) || (ch >= 0xE && ch <= 0x1B) || (ch >= 0x7F && ch <= 0x9F);
	}

	public static boolean isJavaIdentifierPart(char ch) {
		/*
		 * it is a letter it is a currency symbol (such as '$') it is a
		 * connecting punctuation character (such as '_') it is a digit it is a
		 * numeric letter (such as a Roman numeral character) it is a combining
		 * mark it is a non-spacing mark isIdentifierIgnorable returns true for
		 * the character
		 */
		return isLetter(ch) || ch == '$' || ch == '_' || isDigit(ch) || isIdentifierIgnorable(ch);
	}

	public static boolean isJavaIdentifierStart(char ch) {
		// TODO complete this properly
		return isLetter(ch) || ch == '$' || ch == '_' || isDigit(ch);
	}

	public static boolean isLetterOrDigit(char ch) {
		return isLetter(ch) || isDigit(ch);
	}

	public static boolean isWhitespace(char ch) {
		// TODO complete this properly
		switch (ch) {
		case ' ':
		case '\n':
		case '\t':
		case '\r':
		case '\f':
		case '\u001C':
		case '\u001D':
		case '\u001E':
		case '\u001F':
			return true;
		default:
			return false;
		}
	}
}
