package com.colbyskeggs.support;

import java.io.IOException;
import java.io.SyncFailedException;

public interface FileImpl {

	boolean isValid();

	void sync() throws SyncFailedException;

	int read(byte[] b, int off, int len) throws IOException;

	long skip(long n) throws IOException;

	int available() throws IOException;

	void close() throws IOException;

	void write(byte[] b, int off, int len) throws IOException;

}
