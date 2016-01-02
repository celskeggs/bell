package com.colbyskeggs.support;

import java.io.File;
import java.io.IOException;

public class FileSystem {
	public static final class Attributes {
		public boolean readable, writable, executable, directory, file, hidden;
		public long lastModified, length;
	}
	
	public static final class FileSystemAttributes {
		public long total_space, free_space, usable_space;
	}
	
	// TODO: implement these

	public static Attributes attributes(File file) {
		throw new IncompleteImplementationError();
	}
	
	public static FileSystemAttributes fsAttributes(File file) throws IOException {
		throw new IncompleteImplementationError();
	}

	public static boolean atomicNewFile(File file) throws IOException {
		throw new IncompleteImplementationError();
	}

	public static void delete(File file) throws IOException {
		throw new IncompleteImplementationError();
	}

	public static String[] contents(File file) throws IOException {
		throw new IncompleteImplementationError();
	}

	public static void mkdir(File file) throws IOException {
		throw new IncompleteImplementationError();
	}

	public static void rename(File file, File dest) throws IOException {
		throw new IncompleteImplementationError();
	}

	public static void setLastModified(File file, long lastModified) throws IOException {
		throw new IncompleteImplementationError();
	}

	public static void setReadOnly(File file) throws IOException {
		throw new IncompleteImplementationError();
	}

	public static void setWritable(File file, boolean writable, boolean ownerOnly) throws IOException {
		throw new IncompleteImplementationError();
	}

	public static void setReadable(File file, boolean readable, boolean ownerOnly) throws IOException {
		throw new IncompleteImplementationError();
	}

	public static void setExecutable(File file, boolean executable, boolean ownerOnly) throws IOException {
		throw new IncompleteImplementationError();
	}
}
