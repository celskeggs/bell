package java.io;

import com.celskeggs.support.FileImpl;
import com.celskeggs.support.IOSystem;

public final class FileDescriptor {
	public static final FileDescriptor in = new FileDescriptor(IOSystem.in);
	public static final FileDescriptor out = new FileDescriptor(IOSystem.out);
	public static final FileDescriptor err = new FileDescriptor(IOSystem.err);

	FileImpl impl;

	public FileDescriptor() {
		impl = null;
	}

	FileDescriptor(FileImpl impl) {
		this.impl = impl;
	}

	public boolean valid() {
		return impl != null && impl.isValid();
	}

	public void sync() throws SyncFailedException {
		if (!valid()) {
			throw new SyncFailedException("Invalid FileDescriptor");
		}
		impl.sync();
	}

	int read(byte[] b, int off, int len) throws IOException {
		if (!valid()) {
			throw new IOException("Invalid FileDescriptor");
		}
		return impl.read(b, off, len);
	}

	long skip(long n) throws IOException {
		if (!valid()) {
			throw new IOException("Invalid FileDescriptor");
		}
		return impl.skip(n);
	}

	int available() throws IOException {
		if (!valid()) {
			throw new IOException("Invalid FileDescriptor");
		}
		return impl.available();
	}

	void write(byte[] b, int off, int len) throws IOException {
		if (!valid()) {
			throw new IOException("Invalid FileDescriptor");
		}
		impl.write(b, off, len);
	}

	void close() throws IOException {
		if (impl != null) {
			FileImpl i = impl;
			impl = null;
			i.close();
		}
	}
}
