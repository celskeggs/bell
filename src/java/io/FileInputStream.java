package java.io;

import com.colbyskeggs.support.FileSystem;

public class FileInputStream extends InputStream {

	private final FileDescriptor fd;

	public FileInputStream(String name) throws FileNotFoundException {
		this(new File(name));
	}

	public FileInputStream(File file) throws FileNotFoundException {
		this(new FileDescriptor(FileSystem.open(file, FileSystem.READ)));
	}

	public FileInputStream(FileDescriptor fdObj) {
		this.fd = fdObj;
	}

	public int read() throws IOException {
		byte[] b = new byte[1];
		if (read(b, 0, 1) == -1) {
			return -1;
		} else {
			return b[0] & 0xFF;
		}
	}

	public int read(byte[] b, int off, int len) throws IOException {
		return fd.read(b, off, len);
	}

	public long skip(long n) throws IOException {
		return fd.skip(n);
	}

	public int available() throws IOException {
		return fd.available();
	}

	public void close() throws IOException {
		fd.close();
	}

	public final FileDescriptor getFD() throws IOException {
		return fd;
	}

	// public FileChannel getChannel() {} TODO

	protected void finalize() throws IOException {
		fd.close();
	}
}
