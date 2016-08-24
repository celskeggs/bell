package com.celskeggs.bell.vm;

public class VMFormat {
	
	public static final int MAGIC_NUMBER = 0xADD0CA72; // add cats

	public static final int CHUNK_LENGTH_OFFSET = 0;
	// contains a DepClass
	public static final int CHUNK_TYPE_OFFSET = 4;
	// as defined in java.lang.Object
	public static final int CHUNK_HASHCODE_OFFSET = 8;
	// well, second, technically.
	public static final int CHUNK_FIRST_FIELD_OFFSET = 12;
	
	// because it needs extra room to store the REAL length without lost info
	public static final int CHUNK_BYTE_ARRAY_ORIG_LENGTH_OFFSET = 12;
	public static final int CHUNK_BYTE_ARRAY_BODY_OFFSET = 12;

	// widely depended on
	public static final int SERIALIZED_NULL_OFFSET = 0;
	// depended on in Serializer implicit first offset and by explicit uses
	public static final int SERIALIZED_SLAB_OFFSET = 4;
}
