package java.util;

public class StringTokenizer implements Enumeration<Object> {
	// TODO: test this class
	private final char[] str;
	private String delims;
	private final boolean returnDelims;
	private String next_delim_token = null;
	private int i = 0;
	private int token_start_at = 0;

	public StringTokenizer(String str, String delim, boolean returnDelims) {
		this.str = str.toCharArray();
		this.delims = delim;
		this.returnDelims = returnDelims;
	}

	public StringTokenizer(String str, String delim) {
		this(str, delim, false);
	}

	public StringTokenizer(String str) {
		this(str, " \t\n\r\f");
	}

	public boolean hasMoreTokens() {
		if (returnDelims && next_delim_token != null) {
			return true;
		}
		while (true) {
			while (i < str.length && delims.indexOf(str[i]) == -1) {
				i++;
			}
			if (returnDelims || i != token_start_at || i > str.length) {
				break;
			} else {
				// skip the token, because it's just a delimiter and we don't
				// return those
				token_start_at = ++i;
			}
		}
		return i <= str.length;
	}

	public String nextToken() {
		if (returnDelims && next_delim_token != null) {
			String tok = next_delim_token;
			next_delim_token = null;
			return tok;
		}
		if (!hasMoreTokens()) {
			throw new NoSuchElementException();
		}
		if (i == token_start_at) {
			if (!returnDelims) {
				throw new RuntimeException("That shouldn't have happened...");
			}
			// directly return the next delimiter
			return String.valueOf(str[i]);
		}
		String token = new String(str, token_start_at, i - token_start_at);
		if (returnDelims && i < str.length) {
			next_delim_token = String.valueOf(str[i]);
		}
		token_start_at = ++i;
		return token;
	}

	public String nextToken(String delim) {
		this.delims = delim;
		return nextToken();
	}

	public boolean hasMoreElements() {
		return hasMoreTokens();
	}

	public Object nextElement() {
		return nextToken();
	}

	public int countTokens() {
		int total = 0;
		String next_delim_token_save = this.next_delim_token;
		int i_save = this.i;
		int token_start_at_save = this.token_start_at;
		try {
			while (hasMoreTokens()) {
				nextToken();
				total++;
			}
		} finally {
			this.next_delim_token = next_delim_token_save;
			this.i = i_save;
			this.token_start_at = token_start_at_save;
		}
		return total;
	}

}
