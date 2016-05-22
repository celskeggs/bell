package java.lang;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;

import vm.VMNatives;

public class Throwable {
	private final String message;
	private Throwable[] suppressed;
	private StackTraceElement[] trace;
	private Throwable cause;
	private boolean causeProvided = false;

	public Throwable() {
		suppressed = new Throwable[0];
		message = null;
		fillInStackTrace();
	}

	public Throwable(String message) {
		suppressed = new Throwable[0];
		this.message = message;
		fillInStackTrace();
	}

	public Throwable(Throwable cause) {
		suppressed = new Throwable[0];
		this.cause = cause;
		this.message = cause == null ? null : cause.toString();
		causeProvided = true;
		fillInStackTrace();
	}

	public Throwable(String message, Throwable cause) {
		suppressed = new Throwable[0];
		this.cause = cause;
		this.message = message;
		causeProvided = true;
		fillInStackTrace();
	}

	protected Throwable(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		if (enableSuppression) {
			suppressed = new Throwable[0];
		}
		this.message = message;
		this.cause = cause;
		this.causeProvided = true;
		if (writableStackTrace) {
			fillInStackTrace();
		}
	}

	public synchronized final void addSuppressed(Throwable exception) {
		if (exception == null) {
			throw new NullPointerException();
		}
		if (exception == this) {
			throw new IllegalArgumentException("A Throwable cannot suppress itself.");
		}
		if (suppressed == null) {
			// suppression disabled
		} else {
			suppressed = Arrays.copyOf(suppressed, suppressed.length + 1);
			suppressed[suppressed.length - 1] = exception;
		}
	}

	public synchronized final Throwable[] getSuppressed() {
		return suppressed == null ? new Throwable[0] : Arrays.copyOf(suppressed, suppressed.length);
	}

	public Throwable fillInStackTrace() {
		if (trace == null) {
			// not writable
			return this;
		}
		trace = new StackTraceElement[VMNatives.getStackDepth()];
		for (int i = 0; i < trace.length; i++) {
			trace[i] = new StackTraceElement(VMNatives.getStackClass(i), VMNatives.getStackMethod(i),
					VMNatives.getStackFile(i), VMNatives.getStackLine(i));
		}
		return this;
	}

	public StackTraceElement[] getStackTrace() {
		return trace == null ? new StackTraceElement[0] : Arrays.copyOf(trace, trace.length);
	}

	public void setStackTrace(StackTraceElement[] stackTrace) {
		if (stackTrace == null) {
			throw new NullPointerException();
		}
		if (this.trace == null) {
			// not writable
			return;
		}
		for (StackTraceElement e : stackTrace) {
			if (e == null) {
				throw new NullPointerException();
			}
		}
		trace = stackTrace;
	}

	public String getMessage() {
		return message;
	}

	public String getLocalizedMessage() {
		return message;
	}

	public Throwable getCause() {
		return cause;
	}

	public Throwable initCause(Throwable cause) {
		if (cause == this) {
			throw new IllegalArgumentException("A Throwable cannot be its own cause!");
		}
		if (this.causeProvided) {
			throw new IllegalStateException("A cause has already been provided for this Throwable.");
		}
		this.causeProvided = true;
		this.cause = cause;
		return this;
	}

	public String toString() {
		String name = this.getClass().getName();
		String message = getLocalizedMessage();
		return message == null ? name : name + ": " + message;
	}

	public void printStackTrace() {
		this.printStackTrace(System.err);
	}

	public void printStackTrace(PrintStream pstr) {
		try {
			this.printStackTraceInternal(0, pstr, null);
		} catch (IOException e) {
			throw new RuntimeException("Unexpected IOException while printing stack trace.");
		}
	}

	public void printStackTrace(PrintWriter pstr) {
		try {
			this.printStackTraceInternal(0, pstr, null);
		} catch (IOException e) {
			throw new RuntimeException("Unexpected IOException while printing stack trace.");
		}
	}

	private static void indent(int indent, Appendable pstr) throws IOException {
		for (int i = 0; i < indent; i++) {
			pstr.append('\t');
		}
	}

	private void printStackTraceInternal(int indent, Appendable pstr, StackTraceElement[] lastTrace)
			throws IOException {
		pstr.append(this.toString()).append('\n');
		StackTraceElement[] elems = getStackTrace();
		int end_at = elems.length;
		if (lastTrace != null) {
			int last_end = lastTrace.length;
			while (end_at > 0 && last_end > 0 && elems[end_at - 1].equals(lastTrace[last_end - 1])) {
				end_at -= 1;
				last_end -= 1;
			}
		}
		for (int i = 0; i < end_at; i++) {
			indent(indent, pstr);
			pstr.append("\t at").append(elems[i].toString()).append('\n');
		}
		if (end_at < elems.length) {
			indent(indent, pstr);
			pstr.append("\t ...").append(String.valueOf(elems.length - end_at)).append(" more\n");
		}
		for (Throwable suppressed : getSuppressed()) {
			indent(indent + 1, pstr);
			pstr.append("Suppressed: ");
			suppressed.printStackTraceInternal(indent + 1, pstr, null);
		}
		if (cause != null) {
			indent(indent, pstr);
			pstr.append("Caused by: ");
			cause.printStackTraceInternal(indent, pstr, elems);
		}
	}
}
