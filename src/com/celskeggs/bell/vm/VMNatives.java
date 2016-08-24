package com.celskeggs.bell.vm;

import com.celskeggs.bell.vm.data.DatSlab;

public class VMNatives {

	public static DatSlab getRootSlab() {
		return (DatSlab) intToObject(VMFormat.SERIALIZED_SLAB_OFFSET);
	}

	// object at (relative) address zero is NULL

	static native Object intToObject(int obj);

	static native int objectToInt(Object obj);

	private static native int readInt0(int offset);

	private static native void writeInt0(int offset, int value);

	private static native int allocateZeroedChunkRaw(int size);

	public static native StackFrame getCurrentStackFrame();

	// also updates the return address in the previous current stack frame to the return address of this method
	public static native void setCurrentStackFrame(StackFrame frame);

	// size will be rounded up to the nearest 4 byte boundary.
	public static Object allocateZeroedChunk(int size) {
		return intToObject(allocateZeroedChunkRaw(size));
	}

	public static int readLength(Object chunk) {
		return readInt0(objectToInt(chunk) + VMFormat.CHUNK_LENGTH_OFFSET);
	}

	public static int readInt(Object chunk, int offset) {
		if (offset < 0 || offset >= readLength(chunk)) {
			throw new IllegalArgumentException();
		}
		return readInt0(objectToInt(chunk) + offset);
	}

	public static void writeInt(Object chunk, int offset, int value) {
		if (offset < 0 || offset >= readLength(chunk)) {
			throw new IllegalArgumentException();
		}
		writeInt0(objectToInt(chunk) + offset, value);
	}

	public static Object readObject(Object chunk, int offset) {
		return intToObject(readInt(chunk, offset));
	}

	public static void writeObject(Object chunk, int offset, Object value) {
		writeInt(chunk, offset, objectToInt(value));
	}
}
