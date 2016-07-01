package com.celskeggs.bell.support;

import java.io.IOException;
import java.io.SyncFailedException;

import com.celskeggs.bell.vm.VMSandbox;

public class IOSystem {

	private static final class StandardOutput implements FileImpl {
		private final boolean error;

		public StandardOutput(boolean error) {
			this.error = error;
		}

		public boolean isValid() {
			return true;
		}

		public void sync() throws SyncFailedException {
			throw new SyncFailedException("sync not implemented for standard IO streams");
		}

		public int read(byte[] b, int off, int len) throws IOException {
			throw new IOException("not open for reading");
		}

		public long skip(long n) throws IOException {
			throw new IOException("not open for reading");
		}

		public int available() throws IOException {
			throw new IOException("not open for reading");
		}

		public void write(byte[] b, int start, int count) throws IOException {
			if (b == null) {
				throw new NullPointerException();
			}
			if (start < 0 || count < 0 || start + count > b.length) {
				throw new IndexOutOfBoundsException();
			}
			if (start != 0 || count != b.length) {
				byte[] out = new byte[count];
				System.arraycopy(b, start, out, 0, count);
				b = out;
			}
			if (error) {
				VMSandbox.writeStandardError(b);
			} else {
				VMSandbox.writeStandardOutput(b);
			}
		}

		public void close() throws IOException {
			// leave it open
		}
	}

	public static final FileImpl in = new FileImpl() {
		public boolean isValid() {
			return true;
		}

		public void sync() throws SyncFailedException {
			throw new SyncFailedException("sync not implemented for standard IO streams");
		}

		public int read(byte[] b, int off, int len) throws IOException {
			throw new IncompleteImplementationError("Standard input not available");
		}

		public long skip(long n) throws IOException {
			throw new IncompleteImplementationError("Standard input not available");
		}

		public int available() throws IOException {
			throw new IncompleteImplementationError("Standard input not available");
		}

		public void write(byte[] b, int off, int len) throws IOException {
			throw new IOException("not open for writing");
		}

		public void close() throws IOException {
			// leave it open
		}
	};
	public static final FileImpl out = new StandardOutput(false);
	public static final FileImpl err = new StandardOutput(true);

}
