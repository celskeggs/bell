package java.io;

public class PrintWriter extends Writer {

	protected Writer out;
	private final boolean autoFlush;
	private boolean error;
	private boolean closed;

	public PrintWriter(Writer out) {
		this(out, false);
	}

	public PrintWriter(Writer out, boolean autoFlush) {
		this.out = out;
		this.autoFlush = autoFlush;
	}

	public PrintWriter(OutputStream out) {
		this(out, false);
	}

	public PrintWriter(OutputStream out, boolean autoFlush) {
		this(new OutputStreamWriter(out), autoFlush);
	}

	public PrintWriter(String fileName) throws FileNotFoundException {
		this(new File(fileName));
	}

	public PrintWriter(String fileName, String csn) throws FileNotFoundException, UnsupportedEncodingException {
		this(new File(fileName), csn);
	}

	public PrintWriter(File file) throws FileNotFoundException {
		this(new OutputStreamWriter(new FileOutputStream(file)));
	}

	public PrintWriter(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException {
		this(new OutputStreamWriter(new FileOutputStream(file), csn));
	}

	@Override
	public void flush() {
		try {
			out.flush();
		} catch (IOException e) {
			error = true;
		}
	}

	@Override
	public void close() {
		closed = true;
		try {
			out.close();
		} catch (IOException e) {
			error = true;
			e.printStackTrace();
		}
	}

	public boolean checkError() {
		if (!closed) {
			flush();
		}
		return error;
	}

	protected void setError() {
		this.error = true;
	}

	protected void clearError() {
		this.error = false;
	}

	public void write(int c) {
		try {
			out.write(c);
		} catch (IOException e) {
			this.error = true;
		}
	}

	public void write(char[] buf, int off, int len) {
		try {
			out.write(buf, off, len);
		} catch (IOException e) {
			this.error = true;
		}
	}

	public void write(char[] buf) {
		try {
			out.write(buf);
		} catch (IOException e) {
			this.error = true;
		}
	}

	public void write(String s, int off, int len) {
		try {
			out.write(s, off, len);
		} catch (IOException e) {
			this.error = true;
		}
	}

	public void write(String s) {
		try {
			out.write(s);
		} catch (IOException e) {
			this.error = true;
		}
	}

	public void print(boolean b) {
		write(String.valueOf(b));
	}

	public void print(char c) {
		write(String.valueOf(c));
	}

	public void print(int i) {
		write(String.valueOf(i));
	}

	public void print(long l) {
		write(String.valueOf(l));
	}

	public void print(float f) {
		write(String.valueOf(f));
	}

	public void print(double d) {
		write(String.valueOf(d));
	}

	public void print(char[] s) {
		if (s == null) {
			throw new NullPointerException();
		}
		write(s);
	}

	public void print(String s) {
		write(s == null ? "null" : s);
	}

	public void print(Object obj) {
		write(String.valueOf(obj));
	}

	public void println() {
		write(System.getProperty("line.separator"));
		if (autoFlush) {
			flush();
		}
	}

	public void println(boolean x) {
		print(x);
		println();
	}

	public void println(char x) {
		print(x);
		println();
	}

	public void println(int x) {
		print(x);
		println();
	}

	public void println(long x) {
		print(x);
		println();
	}

	public void println(float x) {
		print(x);
		println();
	}

	public void println(char[] x) {
		print(x);
		println();
	}

	public void println(String x) {
		print(x);
		println();
	}

	public void println(Object x) {
		print(x);
		println();
	}

	public PrintWriter printf(String format, Object... args) {
		this.format(format, args);
		return this;
	}

	public PrintWriter format(String format, Object... args) {
		// TODO: optimize
		write(String.format(format, args));
		if (autoFlush) {
			flush();
		}
		return this;
	}

	public PrintWriter append(CharSequence csq) {
		write(csq.toString());
		return this;
	}

	public PrintWriter append(CharSequence csq, int start, int end) {
		write(csq.subSequence(start, end).toString());
		return this;
	}

	public PrintWriter append(char c) {
		write(c);
		return this;
	}
}
