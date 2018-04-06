package com.celskeggs.bell.host;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.bcel.Const;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantDouble;
import org.apache.bcel.classfile.ConstantFloat;
import org.apache.bcel.classfile.ConstantInteger;
import org.apache.bcel.classfile.ConstantLong;
import org.apache.bcel.classfile.ConstantString;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.ConstantValue;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.Repository;

import com.celskeggs.bell.vm.VMFormat;
import com.celskeggs.bell.vm.data.DatClass;
import com.celskeggs.bell.vm.data.DatField;
import com.celskeggs.bell.vm.data.DatMethod;
import com.celskeggs.bell.vm.data.DatSlab;
import com.celskeggs.bell.vm.data.DatString;
import com.celskeggs.bell.vm.data.DatType;

public class Packager {

	private final HashMap<String, ClassEnt> loadedClasses = new HashMap<String, ClassEnt>();
	private final HashMap<String, DatString> strings = new HashMap<String, DatString>();
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
		
		public void removeClass(JavaClass clazz) {
            throw new UnsupportedOperationException();
		}
		
		public org.apache.bcel.util.ClassPath getClassPath() {
            throw new UnsupportedOperationException();
		}
		
		public JavaClass findClass(String className) {
            throw new UnsupportedOperationException();
		}
		
		public JavaClass loadClass(Class<?> clazz) throws ClassNotFoundException {
            throw new UnsupportedOperationException();
		}
		
		public void storeClass(JavaClass clazz) {
            throw new UnsupportedOperationException();
		}
	};

	private static final class ClassEnt {
		public final JavaClass jc;
		public final DatClass dc = new DatClass();

		public ClassEnt(JavaClass jc) {
			this.jc = jc;
		}
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
		loadedClasses.put(binaryName, new ClassEnt(cls));
	}

	private ClassEnt getClass(String name) throws ClassNotFoundException {
		ClassEnt ent = loadedClasses.get(name);
		if (ent == null) {
			throw new ClassNotFoundException("Referenced class " + name + " not loaded yet.");
		}
		return ent;
	}

	private ClassEnt getClass(JavaClass jc) throws ClassNotFoundException {
		return getClass(jc.getClassName());
	}

	private final DatSlab slab = new DatSlab();

	// both recursive and iterated
	private void calculateSize(DatClass dc) {
		if (dc.instance_size != 0) {
			return;
		}
		int size;
		if (dc.super_class == null) {
			size = VMFormat.CHUNK_FIRST_FIELD_OFFSET;
		} else {
			calculateSize(dc.super_class);
			size = dc.super_class.instance_size;
		}
		for (DatField f : dc.fields) {
			size += f.field_type.getSize();
		}
		dc.instance_size = size;
	}

	public DatSlab export() throws ClassNotFoundException {
		slab.datclasses = new DatClass[loadedClasses.size()];
		int i = 0;
		for (ClassEnt e : loadedClasses.values()) {
			slab.datclasses[i++] = e.dc;
			processClass(e.jc, e.dc);
		}
		for (ClassEnt e : loadedClasses.values()) {
			calculateSize(e.dc);
		}
		if (i != slab.datclasses.length) {
			throw new RuntimeException("???");
		}
		slab.datClassDatClass = loadedClasses.get(DatClass.class.getName()).dc;
		if (slab.classConstructor == null || slab.entry_point == null) {
			throw new RuntimeException("Not all static refs were properly populated!");
		}
		return slab;
	}

	private DatString getString(String str) {
		DatString strd = strings.get(str);
		if (strd == null) {
			strd = new DatString();
			strd.data = str.toCharArray();
			strings.put(str, strd);
		}
		return strd;
	}

	private DatType getDatType(Type t) throws ClassNotFoundException {
		DatType dt = new DatType();
		switch (t.getType()) {
		case Const.T_ARRAY:
			dt.tag = DatType.TAG_ARRAY;
			dt.inner_type = getDatType(((ArrayType) t).getElementType());
			break;
		case Const.T_OBJECT:
			dt.tag = DatType.TAG_CLASS;
			dt.class_ref = getClass(((ObjectType) t).getClassName()).dc;
			break;
		case Const.T_BOOLEAN:
			dt.tag = DatType.TAG_BOOLEAN;
			break;
		case Const.T_BYTE:
			dt.tag = DatType.TAG_BYTE;
			break;
		case Const.T_CHAR:
			dt.tag = DatType.TAG_CHAR;
			break;
		case Const.T_DOUBLE:
			dt.tag = DatType.TAG_DOUBLE;
			break;
		case Const.T_FLOAT:
			dt.tag = DatType.TAG_FLOAT;
			break;
		case Const.T_INT:
			dt.tag = DatType.TAG_INT;
			break;
		case Const.T_LONG:
			dt.tag = DatType.TAG_LONG;
			break;
		case Const.T_SHORT:
			dt.tag = DatType.TAG_SHORT;
			break;
		case Const.T_VOID:
			dt.tag = DatType.TAG_VOID;
			break;
		default:
			throw new RuntimeException("Unrecognized type: " + t);
		}
		return dt;
	}

	private void processClass(JavaClass jc, DatClass dc) throws ClassNotFoundException {
		dc.parent = slab;

		dc.super_class = getClass(jc.getSuperClass()).dc;
		dc.name = getString(jc.getClassName().replace('.', '/'));
		dc.flags = jc.getAccessFlags();

		JavaClass[] ifs = jc.getInterfaces();
		dc.interfaces = new DatClass[ifs.length];
		for (int i = 0; i < ifs.length; i++) {
			dc.interfaces[i] = getClass(ifs[i]).dc;
		}

		Field[] fields = jc.getFields();
		dc.fields = new DatField[fields.length];
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];

			DatField df = new DatField();
			df.container = dc;
			processField(f, df);

			dc.fields[i] = df;
		}

		Method[] methods = jc.getMethods();
		dc.methods = new DatMethod[methods.length];
		for (int i = 0; i < methods.length; i++) {
			Method m = methods[i];

			DatMethod dm = new DatMethod();
			dm.container = dc;
			processMethod(m, dm);

			dc.methods[i] = dm;
		}

		dc.assoc_rep = null;
	}

	private void processMethod(Method m, DatMethod dm) throws ClassNotFoundException {
		dm.method_name = getString(m.getName());
		dm.flags = m.getAccessFlags();

		String[] etn = m.getExceptionTable().getExceptionNames();
		dm.thrown_exceptions = new DatClass[etn.length];
		for (int i = 0; i < etn.length; i++) {
			dm.thrown_exceptions[i] = getClass(etn[i]).dc;
		}

		Type[] ats = m.getArgumentTypes();
		dm.parameter_types = new DatType[ats.length];
		for (int i = 0; i < ats.length; i++) {
			dm.parameter_types[i] = getDatType(ats[i]);
		}

		dm.return_type = getDatType(m.getReturnType());

		Code c = m.getCode();
		if (c != null) {
			BytecodeTranscoder tc = new BytecodeTranscoder(this, m, dm);
			tc.transcode(c);
		}
		// TODO
		dm.line_numbers = null;
		dm.implementation_code = null;
		dm.implementation_var_count = 0;
	}

	private void processField(Field f, DatField df) throws ClassNotFoundException {
		df.field_name = getString(f.getName());
		DatType dt = getDatType(f.getType());
		df.field_type = dt;
		df.flags = f.getAccessFlags();

		ConstantValue cv = f.getConstantValue();
		if (cv == null) {
			df.has_constant_value = false;
		} else {
			df.has_constant_value = true;
			Constant c = cv.getConstantPool().getConstant(cv.getConstantValueIndex());
			switch (c.getTag()) {
			case Const.CONSTANT_Long:
				if (dt.tag != DatType.TAG_LONG) {
					throw new RuntimeException("Type mismatch!");
				}
				df.constant_value = ((ConstantLong) c).getBytes();
				break;
			case Const.CONSTANT_Float:
				if (dt.tag != DatType.TAG_FLOAT) {
					throw new RuntimeException("Type mismatch!");
				}
				df.constant_value = Float.floatToIntBits(((ConstantFloat) c).getBytes());
				break;
			case Const.CONSTANT_Double:
				if (dt.tag != DatType.TAG_DOUBLE) {
					throw new RuntimeException("Type mismatch!");
				}
				df.constant_value = Double.doubleToLongBits(((ConstantDouble) c).getBytes());
				break;
			case Const.CONSTANT_Integer:
				if (dt.tag != DatType.TAG_INT) {
					throw new RuntimeException("Type mismatch!");
				}
				df.constant_value = ((ConstantInteger) c).getBytes();
				break;
			case Const.CONSTANT_String:
				if (dt.tag != DatType.TAG_CLASS) { // TODO: check more closely?
					throw new RuntimeException("Type mismatch!");
				}
				int i = ((ConstantString) c).getStringIndex();
				c = cv.getConstantPool().getConstant(i, Const.CONSTANT_Utf8);
				df.constant_string = getString(((ConstantUtf8) c).getBytes());
				break;
			default:
				throw new IllegalStateException("Type of ConstValue invalid: " + c);
			}
		}
	}
}
