package com.celskeggs.bell.vm;

import com.celskeggs.bell.vm.data.DatClass;
import com.celskeggs.bell.vm.data.DatMethod;
import com.celskeggs.bell.vm.data.DatString;
import com.celskeggs.bell.vm.data.DatType;

public class VMAccess {

	public static Class<?> getClassByName(String className) {
		VMClass vmc = getVMClassByName(className);
		return vmc == null ? null : vmc.getRealClass();
	}

	public static VMClass getVMClassByName(String className) {
		for (DatClass cls : VMNatives.getRootSlab().datclasses) {
			if (className.equals(getStringByDat(cls.name))) {
				return getVMClassByDatClass(cls);
			}
		}
		return null;
	}
	
	public static String getStringByDat(DatString str) {
		String s = str.assoc;
		if (s != null) {
			return s;
		}
		return str.assoc = new String(str.data);
	}

	public static VMClass getVMClassByDatClass(DatClass cls) {
		if (cls == null) {
			return null;
		}
		Object o = cls.assoc_rep;
		if (o != null) {
			return (VMClass) o;
		}
		VMClass vmcn = new VMClass.Java(cls);
		cls.assoc_rep = vmcn;
		return vmcn;
	}

	public static VMClass getVMClassByDatType(DatType cls) {
		if (cls == null) {
			return null;
		}
		Object o = cls.assoc_rep;
		if (o != null) {
			return (VMClass) o;
		}
		switch (cls.tag) {
		case DatType.TAG_ARRAY:
			return getVMClassByDatType(cls.inner_type).getArrayOf();
		case DatType.TAG_CLASS:
			return getVMClassByDatClass(cls.class_ref);
		case DatType.TAG_BOOLEAN:
			return VMClass.BOOLEAN;
		case DatType.TAG_BYTE:
			return VMClass.BYTE;
		case DatType.TAG_CHAR:
			return VMClass.CHAR;
		case DatType.TAG_SHORT:
			return VMClass.SHORT;
		case DatType.TAG_INT:
			return VMClass.INT;
		case DatType.TAG_FLOAT:
			return VMClass.FLOAT;
		case DatType.TAG_LONG:
			return VMClass.LONG;
		case DatType.TAG_DOUBLE:
			return VMClass.DOUBLE;
		case DatType.TAG_VOID:
			return VMClass.VOID;
		default:
			throw new RuntimeException("Unknown DatType tag: " + cls.tag);
		}
	}

	public static int invoke1(DatMethod nc, int param) {
		StackFrame frame = new StackFrame();
		frame.return_entry = VMNatives.getCurrentStackFrame();
		frame.locals = new int[nc.implementation_var_count];
		frame.locals[0] = param;
		// get contents of the byte array
		frame.next_address = VMNatives.objectToInt(nc.implementation_code) + VMFormat.CHUNK_FIRST_FIELD_OFFSET;
		VMNatives.setCurrentStackFrame(frame);
		return frame.return_value_low;
	}
}
