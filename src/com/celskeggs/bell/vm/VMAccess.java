package com.celskeggs.bell.vm;

public class VMAccess {

	// TODO: should this exist?
	public static Class getClassByName(String className) {
		VMClass vmc = getVMClassByName(className);
		return vmc == null ? null : vmc.getRealClass();
	}

	public static int getClassCount() {
		return VMNatives.getCodeInt(VMFormat.CLASS_TABLE_LENGTH_OFFSET);
	}

	public static VMClass getVMClassByName(String className) {
		int count = getClassCount();
		for (int i = 0; i < count; i++) {
			if (className.equals(getVMClassName(i))) {
				return getVMClassByID(i);
			}
		}
		return null;
	}

	public static String getVMClassName(int vmid) {
		return VMDispatch.getStringByID(VMNatives.getCodeInt(getClassEntity(vmid) + 4));
	}

	public static VMClass getVMClassByID(int id) {
		if (VMAccess.globalClasses == null) {
			VMAccess.globalClasses = new VMClass.Java[VMNatives.getCodeInt(VMFormat.CLASS_TABLE_LENGTH_OFFSET)];
		} else if (VMAccess.globalClasses[id] != null) {
			return VMAccess.globalClasses[id];
		}
		return VMAccess.globalClasses[id] = new VMClass.Java(id);
	}

	private static VMClass.Java[] globalClasses;

	public static int getClassEntity(int classid) {
		int clstab = VMNatives.getCodeInt(VMFormat.CLASS_TABLE_POINTER_OFFSET);
		return VMNatives.getCodeInt(clstab + 4 * classid);
	}

	public static int getNullConstructor(int id) {
		return VMNatives.getCodeInt(getClassEntity(id) + 8);
	}

	public static int getVMClassFlags(int id) {
		return VMNatives.getCodeInt(getClassEntity(id) + 12);
	}

	public static VMClass getSuperClass(int id) {
		int sup = VMNatives.getCodeInt(getClassEntity(id) + 16);
		// -1 means no superclass - root
		return sup == -1 ? null : VMAccess.getVMClassByID(sup);
	}

	public static int getInterfaceCount(int id) {
		return VMNatives.getCodeInt(getClassEntity(id) + 20);
	}

	public static int getInterfaceN(int id, int n) {
		int iarray = VMNatives.getCodeInt(getClassEntity(id) + 24);
		return VMNatives.getCodeInt(iarray + 4 * n);
	}

	public static int getMethodCount(int id) {
		return VMNatives.getCodeInt(getClassEntity(id) + 28);
	}

	public static int getMethodOffset(int id) {
		int marray = VMNatives.getCodeInt(getClassEntity(id) + 32);
		return VMNatives.getCodeInt(marray + 4 * id);
	}

	public static int getFieldCount(int id) {
		return VMNatives.getCodeInt(getClassEntity(id) + 36);
	}

	public static int getFieldOffset(int id) {
		int farray = VMNatives.getCodeInt(getClassEntity(id) + 40);
		return VMNatives.getCodeInt(farray + 4 * id);
	}
}
