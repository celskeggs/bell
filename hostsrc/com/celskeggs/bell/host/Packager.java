package com.celskeggs.bell.host;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.bcel.util.Repository;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

import com.celskeggs.bell.vm.VMFormat;

public class Packager {

	private byte[] data = new byte[1024];
	private int index = 0;
	private final ArrayList<String> strings = new ArrayList<String>();
	private final HashMap<String, ClassEnt> loadedClasses = new HashMap<String, ClassEnt>();
	private final Repository repo = new Repository() {

		public JavaClass loadClass(String className) throws ClassNotFoundException {
			ClassEnt cls = loadedClasses.get(className.replace('.', '/'));
			if (cls == null) {
				throw new ClassNotFoundException(className);
			}
			return cls.jc;
		}

		public void clear() {
			throw new UnsupportedOperationException();
		}
	};

	private static final class ClassEnt {
		public final JavaClass jc;
		public int linkTarget = -1;
		public final int index;

		public ClassEnt(int index, JavaClass jc) {
			this.index = index;
			this.jc = jc;
		}

		public int calculateLength() {

		}
	}

	private void growBytes() {
		if (index >= data.length) {
			data = Arrays.copyOf(data, data.length * 2);
		}
	}

	private void writeByte(byte b) {
		growBytes();
		data[index++] = b;
	}

	private void writeBytes(byte[] bytes) {
		while (index + bytes.length >= data.length) {
			growBytes();
		}
		System.arraycopy(bytes, 0, data, index, bytes.length);
		index += bytes.length;
	}

	private void writeInt(int i) {
		growBytes();
		data[index++] = (byte) (i >> 24);
		data[index++] = (byte) (i >> 16);
		data[index++] = (byte) (i >> 8);
		data[index++] = (byte) (i >> 0);
	}

	private void patchInt(int offset, int value) {
		data[offset + 0] = (byte) (value >> 24);
		data[offset + 1] = (byte) (value >> 16);
		data[offset + 2] = (byte) (value >> 8);
		data[offset + 3] = (byte) (value >> 0);
	}

	private void writeLong(long l) {
		writeInt((int) (l >> 32));
		writeInt((int) (l >> 0));
	}

	private void zeroFill(int zeroes) {
		Arrays.fill(data, index, index + zeroes, (byte) 0);
		index += zeroes;
	}

	public Packager() {
		writeLong(VMFormat.MAGIC_NUMBER);
		// skip to the end of the header - fill with zeros
		// we'll fill it in later.
		zeroFill(VMFormat.HEADER_LENGTH - index);
	}

	private int addString(String str) {
		int si = strings.indexOf(str);
		if (si != -1) {
			return si;
		}
		si = strings.size();
		strings.add(str);
		return si;
	}

	private void writeStringRef(String str) {
		writeInt(addString(str));
	}

	public void loadClassFromStream(InputStream stream, String binaryName) throws IOException {
		if (loadedClasses.containsKey(binaryName)) {
			throw new IllegalStateException("Class already loaded: " + binaryName);
		}
		ClassParser parser = new ClassParser(stream, binaryName);
		JavaClass cls = parser.parse();
		if (!binaryName.equals(cls.getClassName())) {
			throw new ClassFormatException("Class name does not match expected name!");
		}
		cls.setRepository(repo);
		loadedClasses.put(binaryName, new ClassEnt(loadedClasses.size(), cls));
	}

	private ClassEnt getClass(String name) throws ClassNotFoundException {
		ClassEnt ent = loadedClasses.get(name);
		if (ent == null) {
			throw new ClassNotFoundException("Referenced class " + name + " not loaded yet.");
		}
		return ent;
	}

	private void addClass(ClassEnt cls) throws ClassNotFoundException {
		int interfaceIndex = index;
		String[] ifs = cls.jc.getInterfaceNames();
		for (String s : ifs) {
			writeInt(getClass(s).index);
		}
		cls.linkTarget = index;
		// reflects VMFormat class
		writeInt(cls.calculateLength());
		writeStringRef(cls.jc.getClassName());
		writeInt(cls.jc.getAccessFlags());
		writeInt(ifs.length);
		writeInt(interfaceIndex);
		if (cls.jc.getSuperclassNameIndex() == 0) {
			// root
			writeInt(-1);
		} else {
			writeInt(getClass(cls.jc.getSuperclassName()).index);
		}
	}

	public byte[] compile() throws ClassNotFoundException {
		try {
			for (ClassEnt cls : loadedClasses.values()) {
				addClass(cls);
			}

			writeStringsAndHeader();
			writeClassTable();
			// TODO: patch entry point

			return Arrays.copyOf(data, index);
		} finally {
			data = null; // don't let it be recompiled
		}
	}

	private void writeStringsAndHeader() {
		int[] strinds = new int[strings.size()];
		for (int i = 0; i < strinds.length; i++) {
			strinds[i] = index;
			byte[] str = strings.get(i).getBytes();
			writeInt(str.length);
			writeBytes(str);
		}
		patchInt(VMFormat.STRING_TABLE_POINTER_OFFSET, index);
		patchInt(VMFormat.STRING_TABLE_LENGTH_OFFSET, strinds.length);
		for (int ptr : strinds) {
			writeInt(ptr);
		}
	}

	private void writeClassTable() {
		patchInt(VMFormat.CLASS_TABLE_POINTER_OFFSET, index);
		patchInt(VMFormat.CLASS_TABLE_LENGTH_OFFSET, loadedClasses.size());
		for (ClassEnt cls : loadedClasses.values()) {
			if (cls.linkTarget == -1) {
				throw new RuntimeException("Unlinked class detected.");
			}
			writeInt(cls.linkTarget);
		}
	}
}
