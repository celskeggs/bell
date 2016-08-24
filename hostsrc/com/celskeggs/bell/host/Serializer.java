package com.celskeggs.bell.host;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.celskeggs.bell.vm.VMFormat;
import com.celskeggs.bell.vm.data.DatClass;
import com.celskeggs.bell.vm.data.DatField;
import com.celskeggs.bell.vm.data.DatSlab;
import com.celskeggs.bell.vm.data.DatType;

public class Serializer {
	private Object[] patches;
	private byte[] bytes;
	private int index;
	private final HashMap<Object, Integer> offsets = new HashMap<Object, Integer>();
	private final ArrayList<Object> remaining = new ArrayList<Object>();
	private final HashMap<Class<?>, DatClass> classes = new HashMap<Class<?>, DatClass>();

	public Serializer() {
		patches = new Object[1024];
		bytes = new byte[1024];
		writeInt(VMFormat.MAGIC_NUMBER);
	}

	private void growLarger(int avail) {
		if (index + avail > bytes.length) {
			bytes = Arrays.copyOf(bytes, Math.max(bytes.length, index + avail) * 2);
			patches = Arrays.copyOf(patches, bytes.length);
		}
	}

	private void writeInt(int i) {
		growLarger(4);
		bytes[index++] = (byte) (i >> 24);
		bytes[index++] = (byte) (i >> 16);
		bytes[index++] = (byte) (i >> 8);
		bytes[index++] = (byte) (i >> 0);
	}

	private void setShort(int ptr, short s) {
		bytes[ptr++] = (byte) (s >> 8);
		bytes[ptr] = (byte) s;
	}
	
	private int getInt(int ptr) {
		return ((bytes[ptr + 0] & 0xFF) << 24) | ((bytes[ptr + 1] & 0xFF) << 16) | ((bytes[ptr + 2] & 0xFF) << 8) | ((bytes[ptr + 3] & 0xFF) << 0);
	} 

	private void setInt(int ptr, int i) {
		bytes[ptr++] = (byte) (i >> 24);
		bytes[ptr++] = (byte) (i >> 16);
		bytes[ptr++] = (byte) (i >> 8);
		bytes[ptr] = (byte) i;
	}

	private void setLong(int ptr, long l) {
		bytes[ptr++] = (byte) (l >> 56);
		bytes[ptr++] = (byte) (l >> 48);
		bytes[ptr++] = (byte) (l >> 40);
		bytes[ptr++] = (byte) (l >> 32);
		bytes[ptr++] = (byte) (l >> 24);
		bytes[ptr++] = (byte) (l >> 16);
		bytes[ptr++] = (byte) (l >> 8);
		bytes[ptr] = (byte) l;
	}

	private void setIntPatched(int ptr, Object obj) {
		setInt(ptr, 0);
		if (obj != null) {
			addObject(obj);
			patches[ptr] = obj;
		}
	}

	private int reserveSpace(int size) {
		growLarger(size);
		int initial_pointer = index;
		index += size;
		Arrays.fill(bytes, initial_pointer, index, (byte) 0);
		return initial_pointer;
	}

	private static final String prefix;
	static {
		String dname = DatSlab.class.getName();
		prefix = dname.substring(0, dname.lastIndexOf('.') + 1);
	}

	private boolean isSerializableClass(Class<?> cls) {
		return cls == Object.class || cls.getName().startsWith(prefix);
	}

	private boolean isSerializableObject(Object o) {
		return o == null || isSerializableClass(o.getClass());
	}

	public void addObject(Object o) {
		if (o == null || !isSerializableObject(o)) {
			throw new IllegalArgumentException("Not a serializable object!");
		}
		if (offsets.containsKey(o) || remaining.contains(o)) {
			return;
		}
		remaining.add(o);
	}

	public void addClass(Class<?> cls, DatClass cls2) {
		if (classes.containsKey(cls)) {
			throw new IllegalArgumentException("Already registered: " + cls);
		}
		addObject(cls2);
		classes.put(cls, cls2);
	}

	private void processObject(Object obj) throws ReflectiveOperationException, IllegalAccessException {
		if (obj == null || !isSerializableObject(obj)) {
			throw new RuntimeException("Not a serializable object!");
		}
		Class<?> javaClass = obj.getClass();
		if (javaClass.isArray()) {
			processArray(obj);
			return;
		}
		DatClass classFor = classes.get(javaClass);
		if (classFor == null) {
			throw new RuntimeException("No DatClass instance found for " + obj.getClass() + "!");
		}
		if (classFor.instance_size < VMFormat.CHUNK_FIRST_FIELD_OFFSET) {
			throw new RuntimeException("Invalid DatClass for " + obj.getClass()
					+ ": instance_size is less than CHUNK_FIRST_FIELD_OFFSET!");
		}
		int ref = reserveSpace(classFor.instance_size);
		setInt(ref + VMFormat.CHUNK_LENGTH_OFFSET, classFor.instance_size);
		setIntPatched(ref + VMFormat.CHUNK_TYPE_OFFSET, classFor);
		// to be calculated on demand
		setInt(ref + VMFormat.CHUNK_HASHCODE_OFFSET, 0);
		populateObjectFields(ref + VMFormat.CHUNK_FIRST_FIELD_OFFSET, obj, classFor,
				classFor.instance_size - VMFormat.CHUNK_FIRST_FIELD_OFFSET);
	}
	
	private DatType convertClassToDatType(Class<?> cls) {
		DatType dt = new DatType();
		if (cls.isPrimitive()) {
			if (cls == boolean.class) {
				dt.tag = DatType.TAG_BOOLEAN;
			} else if (cls == byte.class) {
				dt.tag = DatType.TAG_BYTE;
			} else if (cls == char.class) {
				dt.tag = DatType.TAG_CHAR;
			} else if (cls == short.class) {
				dt.tag = DatType.TAG_SHORT;
			} else if (cls == int.class) {
				dt.tag = DatType.TAG_INT;
			} else if (cls == float.class) {
				dt.tag = DatType.TAG_FLOAT;
			} else if (cls == long.class) {
				dt.tag = DatType.TAG_LONG;
			} else if (cls == double.class) {
				dt.tag = DatType.TAG_DOUBLE;
			} else if (cls == void.class) {
				dt.tag = DatType.TAG_VOID;
			} else {
				throw new RuntimeException("Unknown primitive type: " + cls);
			}
		} else if (cls.isArray()) {
			dt.tag = DatType.TAG_ARRAY;
			dt.inner_type = convertClassToDatType(cls.getComponentType());
		} else {
			dt.tag = DatType.TAG_CLASS;
			dt.class_ref = classes.get(cls);
			if (dt.class_ref == null) {
				throw new RuntimeException("No DatClass instance found for " + cls + "!");
			}
		}
		return dt;
	}
	
	private void processArray(Object object) {
		DatType t = convertClassToDatType(object.getClass().getComponentType());
		int chunk_size = VMFormat.CHUNK_FIRST_FIELD_OFFSET;
		if (t.tag == DatType.TAG_BOOLEAN) {
			// tacks 4 bytes on before the body to store the original byte array length
			chunk_size += (((boolean[]) object).length + 7) / 8 + 4;
		} else {
			chunk_size += t.getSize() * Array.getLength(object);
		}
		int ref = reserveSpace(chunk_size);
		setInt(ref + VMFormat.CHUNK_LENGTH_OFFSET, chunk_size);
		setIntPatched(ref + VMFormat.CHUNK_TYPE_OFFSET, t);
		// to be calculated on demand
		setInt(ref + VMFormat.CHUNK_HASHCODE_OFFSET, 0);
		if (object instanceof boolean[]) {
			boolean[] bits = (boolean[]) object;
			setInt(ref + VMFormat.CHUNK_BYTE_ARRAY_ORIG_LENGTH_OFFSET, bits.length);
			int base = ref + VMFormat.CHUNK_BYTE_ARRAY_BODY_OFFSET;
			for (int i = 0; i < bits.length; i++) {
				bytes[base + (i >> 3)] |= (1 << (i & 7));
			}
		} else if (object instanceof Object[]) {
			Object[] arr = (Object[]) object;
			for (int i = 0; i < arr.length; i++) {
				setIntPatched(ref + VMFormat.CHUNK_FIRST_FIELD_OFFSET + 4 * i, arr[i]);
			}
		} else if (object instanceof byte[]) {
			byte[] arr = (byte[]) object;
			System.arraycopy(arr, 0, bytes, ref + VMFormat.CHUNK_FIRST_FIELD_OFFSET, arr.length);
		} else if (object instanceof short[]) {
			short[] arr = (short[]) object;
			for (int i = 0; i < arr.length; i++) {
				setShort(ref + VMFormat.CHUNK_FIRST_FIELD_OFFSET + 2 * i, arr[i]);
			}
		} else if (object instanceof char[]) {
			char[] arr = (char[]) object;
			for (int i = 0; i < arr.length; i++) {
				setShort(ref + VMFormat.CHUNK_FIRST_FIELD_OFFSET + 2 * i, (short) arr[i]);
			}
		} else if (object instanceof int[]) {
			int[] arr = (int[]) object;
			for (int i = 0; i < arr.length; i++) {
				setInt(ref + VMFormat.CHUNK_FIRST_FIELD_OFFSET + 4 * i, arr[i]);
			}
		} else if (object instanceof float[]) {
			float[] arr = (float[]) object;
			for (int i = 0; i < arr.length; i++) {
				setInt(ref + VMFormat.CHUNK_FIRST_FIELD_OFFSET + 4 * i, Float.floatToIntBits(arr[i]));
			}
		} else if (object instanceof long[]) {
			long[] arr = (long[]) object;
			for (int i = 0; i < arr.length; i++) {
				setLong(ref + VMFormat.CHUNK_FIRST_FIELD_OFFSET + 8 * i, arr[i]);
			}
		} else if (object instanceof double[]) {
			double[] arr = (double[]) object;
			for (int i = 0; i < arr.length; i++) {
				setLong(ref + VMFormat.CHUNK_FIRST_FIELD_OFFSET + 8 * i, Double.doubleToLongBits(arr[i]));
			}
		} else {
			throw new RuntimeException("Unknown array: " + object);
		}
	}

	private void populateObjectFields(int ptr, Object obj, DatClass cls, int expected_bytes)
			throws ReflectiveOperationException, IllegalAccessException {
		int found_bytes = 0;
		Class<?> clsR = obj.getClass();
		for (DatField f : cls.fields) {
			Field fR = clsR.getField(new String(f.field_name.data));
			Object value = fR.get(obj);
			found_bytes += f.field_type.getSize();
			switch (f.field_type.tag) {
			case DatType.TAG_ARRAY:
			case DatType.TAG_CLASS:
				setIntPatched(ptr + found_bytes, value);
				break;
			case DatType.TAG_BOOLEAN:
				bytes[ptr + found_bytes] = (byte) (((Boolean) value) ? 1 : 0);
				break;
			case DatType.TAG_BYTE:
				bytes[ptr + found_bytes] = (Byte) value;
				break;
			case DatType.TAG_CHAR:
				setShort(ptr + found_bytes, (short) (char) (Character) value);
				break;
			case DatType.TAG_SHORT:
				setShort(ptr + found_bytes, (short) (Short) value);
				break;
			case DatType.TAG_INT:
				setInt(ptr + found_bytes, (Integer) value);
				break;
			case DatType.TAG_FLOAT:
				setInt(ptr + found_bytes, Float.floatToIntBits((Float) value));
				break;
			case DatType.TAG_LONG:
				setLong(ptr + found_bytes, (Long) value);
				break;
			case DatType.TAG_DOUBLE:
				setLong(ptr + found_bytes, Double.doubleToLongBits((Double) value));
				break;
			default:
				throw new RuntimeException("Unrecognized field type tag: " + f.field_type.tag);
			}
		}
		if (expected_bytes != found_bytes) {
			throw new RuntimeException("Field size mismatch!");
		}
	}

	public void processAll() throws ReflectiveOperationException, IllegalAccessException {
		while (!remaining.isEmpty()) {
			processObject(remaining.remove(remaining.size() - 1));
		}
		for (int i = 0; i < patches.length; i++) {
			Object o = patches[i];
			if (o != null) {
				Integer offset = offsets.get(o);
				if (offset != null) {
					patches[i] = null;
					setInt(i, getInt(i) + offset);
				}
			}
		}
	}
	
	public byte[] compile() throws ReflectiveOperationException, IllegalAccessException {
		processAll();
		for (int i = 0; i < patches.length; i++) {
			if (patches[i] != null) {
				throw new RuntimeException("Object somehow missing: " + patches[i]);
			}
		}
		return Arrays.copyOf(bytes, index);
	}
}
