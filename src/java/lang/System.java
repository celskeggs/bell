package java.lang;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;

import vm.VMStandardOutputStream;

public final class System {

	public static final PrintStream out = new PrintStream(new FileOutputStream(FileDescriptor.out));
	public static final PrintStream err = new PrintStream(new FileOutputStream(FileDescriptor.err));

	public static native int identityHashCode(Object aThis);

	private System() {
	}

	public static native long currentTimeMillis();

	public static native void arraycopy(Object src, int srcOffset, Object dst, int dstOffset, int length);

	public static String getProperty(String key) {
		if (key == null) {
			throw new NullPointerException();
		}
		if (key.length() == 0) {
			throw new IllegalArgumentException();
		}
		if (key.equals("line.separator")) {
			return "\n"; // duplicated in PrintStream
		} else if (key.equals("file.separator")) {
			return "/";
		} else if (key.equals("path.separator")) {
			return ":";
		} else if (key.equals("user.dir")) {
			return "/tmp"; // TODO: determine current working directory PROPERLY
		} else if (key.equals("java.io.tmpdir")) {
			return "/tmp";
		}
		// TODO: Implement this better
		return null;
	}

	public static String getProperty(String key, String default_) {
		String out = getProperty(key);
		return out == null ? default_ : out;
	}

	public static void exit(int status) {
		Runtime.getRuntime().exit(status);
	}

	public static void gc() {
		Runtime.getRuntime().gc();
	}
}
