package com.celskeggs.bell.vm.data;

public class DatType {
	public static final char TAG_BYTE = 'B';
	public static final char TAG_CHAR = 'C';
	public static final char TAG_DOUBLE = 'D';
	public static final char TAG_FLOAT = 'F';
	public static final char TAG_INT = 'I';
	public static final char TAG_LONG = 'J';
	public static final char TAG_CLASS = 'L';
	public static final char TAG_SHORT = 'S';
	public static final char TAG_BOOLEAN = 'Z';
	public static final char TAG_ARRAY = '[';
	public static final char TAG_VOID = 'V';

	public char tag;
	public DatClass class_ref;
	public DatType inner_type;

	public Object assoc_rep;

	public boolean isVoid() {
		return tag == TAG_VOID;
	}

	public int getSize() {
		switch (tag) {
		case TAG_BOOLEAN:
		case TAG_BYTE:
			return 1;
		case TAG_SHORT:
		case TAG_CHAR:
			return 2;
		case TAG_CLASS:
		case TAG_ARRAY:
		case TAG_INT:
		case TAG_FLOAT:
			return 4;
		case TAG_LONG:
		case TAG_DOUBLE:
			return 8;
		case TAG_VOID:
			throw new RuntimeException("Void has no size!");
		default:
			throw new RuntimeException("Unexpected tag: " + tag);
		}
	}
}
