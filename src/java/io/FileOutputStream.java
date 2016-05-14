package java.io;

import com.colbyskeggs.support.FileSystem;

public class FileOutputStream extends OutputStream {

	private final FileDescriptor fd;

	public FileOutputStream(String name) throws FileNotFoundException {
		this(new File(name), false);
	}

	public FileOutputStream(String name, boolean append) throws FileNotFoundException {
		this(new File(name), append);
	}

	public FileOutputStream(File file) throws FileNotFoundException {
		this(file, false);
	}

	public FileOutputStream(File file, boolean append) throws FileNotFoundException {
		this(new FileDescriptor(FileSystem.open(file, append ? FileSystem.APPEND : FileSystem.WRITE)));
	}

	public FileOutputStream(FileDescriptor fdObj) {
		this.fd = fdObj;
	}

	public void write(int b) throws IOException {
		byte[] bs = new byte[] { (byte) b };
		write(bs, 0, 1);
	}

	public void write(byte[] b, int off, int len) throws IOException {
		fd.write(b, off, len);
	}

	public void close() throws IOException {
		fd.close();
	}

	public final FileDescriptor getFD() throws IOException {
		return fd;
	}

	// public FileChannel getChannel();

	protected void finalize() throws IOException {
		fd.close();
	}
}
