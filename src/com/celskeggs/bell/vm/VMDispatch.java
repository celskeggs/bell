package com.celskeggs.bell.vm;

public class VMDispatch {

	public static Class<?> getClassByID(int id) {
		return VMAccess.getVMClassByID(id).getRealClass();
	}

	private static String[] globalStrings;

	public static String getStringByID(int id) {
		if (globalStrings == null) {
			globalStrings = new String[VMNatives.getCodeInt(VMFormat.STRING_TABLE_LENGTH_OFFSET)];
		} else if (globalStrings[id] != null) {
			return globalStrings[id];
		}
		int sptr = VMNatives.getCodeInt(VMNatives.getCodeInt(VMFormat.STRING_TABLE_POINTER_OFFSET) + 4 * id);
		byte[] bytes = new byte[VMNatives.getCodeInt(sptr)];
		sptr += 4;
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = VMNatives.getCodeByte(sptr + i);
		}
		return globalStrings[id] = new String(bytes);
	}

	public static Object rawNewObject(int classid) {
		return VMNatives.allocateStructure(VMAccess.getClassEntity(classid));
	}
	
	public static void vmEntryPoint() {
		// ???
	}
}
