package java.lang.reflect;

import com.celskeggs.support.IncompleteImplementationError;

public final class Field extends AccessibleObject implements Member {
	private final Class<?> parent;
	private final String name;
	private final int modifiers;
	private final Class<?> type;

	private Field(Class<?> parent, String name, int mod, Class<?> type) {
		this.parent = parent;
		this.name = name;
		this.modifiers = mod;
		this.type = type;
	}

	public Class<?> getDeclaringClass() {
		return parent;
	}

	public String getName() {
		return name;
	}

	private static final int MODIFIERS_ALLOWED = Modifier.fieldModifiers();

	public int getModifiers() {
		return modifiers & MODIFIERS_ALLOWED;
	}

	private static final int ACC_ENUM = 0x4000;
	private static final int ACC_SYNTHETIC = 0x1000;

	public boolean isEnumConstant() {
		return (modifiers & ACC_ENUM) != 0;
	}

	public boolean isSynthetic() {
		return (modifiers & ACC_SYNTHETIC) != 0;
	}

	public Class<?> getType() {
		return type;
	}

	public boolean equals(Object obj) {
		return obj instanceof Field && parent == ((Field) obj).parent && name.equals(((Field) obj).name)
				&& type == ((Field) obj).parent;
	}

	public int hashCode() {
		return parent.getName().hashCode() ^ name.hashCode();
	}

	public String toString() {
		String out = type.getName() + parent.getName() + "." + name;
		int mod = getModifiers();
		if (mod != 0) {
			return Modifier.toString(mod) + " " + out;
		} else {
			return out;
		}
	}

	public Object get(Object obj) throws IllegalArgumentException, IllegalAccessException {
		throw new IncompleteImplementationError();
	}

	public boolean getBoolean(Object obj) throws IllegalArgumentException, IllegalAccessException {
		throw new IncompleteImplementationError();
	}

	public byte getByte(Object obj) throws IllegalArgumentException, IllegalAccessException {
		throw new IncompleteImplementationError();
	}

	public char getChar(Object obj) throws IllegalArgumentException, IllegalAccessException {
		throw new IncompleteImplementationError();
	}

	public short getShort(Object obj) throws IllegalArgumentException, IllegalAccessException {
		throw new IncompleteImplementationError();
	}

	public int getInt(Object obj) throws IllegalArgumentException, IllegalAccessException {
		throw new IncompleteImplementationError();
	}

	public long getLong(Object obj) throws IllegalArgumentException, IllegalAccessException {
		throw new IncompleteImplementationError();
	}

	public float getFloat(Object obj) throws IllegalArgumentException, IllegalAccessException {
		throw new IncompleteImplementationError();
	}

	public double getDouble(Object obj) throws IllegalArgumentException, IllegalAccessException {
		throw new IncompleteImplementationError();
	}

	public void set(Object obj, Object value) throws IllegalArgumentException, IllegalAccessException {
		throw new IncompleteImplementationError();
	}

	public void setBoolean(Object obj, boolean z) throws IllegalArgumentException, IllegalAccessException {
		throw new IncompleteImplementationError();
	}

	public void setByte(Object obj, byte b) throws IllegalArgumentException, IllegalAccessException {
		throw new IncompleteImplementationError();
	}

	public void setChar(Object obj, char c) throws IllegalArgumentException, IllegalAccessException {
		throw new IncompleteImplementationError();
	}

	public void setShort(Object obj, short s) throws IllegalArgumentException, IllegalAccessException {
		throw new IncompleteImplementationError();
	}

	public void setInt(Object obj, int i) throws IllegalArgumentException, IllegalAccessException {
		throw new IncompleteImplementationError();
	}

	public void setLong(Object obj, long l) throws IllegalArgumentException, IllegalAccessException {
		throw new IncompleteImplementationError();
	}

	public void setFloat(Object obj, float f) throws IllegalArgumentException, IllegalAccessException {
		throw new IncompleteImplementationError();
	}

	public void setDouble(Object obj, double d) throws IllegalArgumentException, IllegalAccessException {
		throw new IncompleteImplementationError();
	}
}
