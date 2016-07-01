package com.celskeggs.bell.vm;

/* Code format:
 * [0-7] MAGIC NUMBER: 0x70A27E52 (toaster)
 * [8-11] offset to string table
 * [12-15] length of string table
 * [16-19] offset to class table
 * [20-23] length of class table
 * [24-27] pointer to code entry point
 * 
 * [stringtab+0] pointer to first string array
 * [stringtab+4] pointer to second string array
 * [stringtab+n] etc.
 * 
 * [strarray+0] string length
 * [strarray+4] first byte
 * [strarray+5] second byte
 * [strarray+6] third byte
 * [strarray+n] etc.
 * 
 * [classtab+0] pointer to first class entry
 * [classtab+4] pointer to second class entry
 * [classtab+n] etc.
 * 
 * [class+0] length of class instance (not including type pointer, and in bytes)
 * [class+4] string ID for class name
 * [class+8] null constructor ID
 * [class+12] flags
 * [class+16] superclass
 * [class+20] interface count
 * [class+24] interface list ref (of class IDs)
 * [class+28] method count
 * [class+32] method list ref (of method ptrs)
 * [class+36] field count
 * [class+40] field list ref (of field ptrs)
 * 
 * [method+0] string ID for method name
 * [method+4] flags
 * [method+8] parameter count
 * [method+12] parameter list ref (of type nums)
 * [method+16] return type num
 * [method+20] declared exception list count
 * [method+24] declared exception list ref (of class ptrs)
 * [method+28] entry point ptr
 * [method+32] TODO ... (cached stuff like max stack size? maybe?)
 * 
 * [field+0] string ID for method name
 * [field+4] flags
 * [field+8] field type num
 * [field+12] constant value type tag
 * [field+16] constant value actual value (up to 8 bytes)
 * 
 * TODO: format of type num (32 bits alone ... but can have pointer in complex cases)
 */

public class VMFormat {

	// Offsets are used in Packager.
	public static final long MAGIC_NUMBER = 0x70A27E52;
	public static final int STRING_TABLE_POINTER_OFFSET = 8;
	public static final int STRING_TABLE_LENGTH_OFFSET = 12;
	public static final int CLASS_TABLE_POINTER_OFFSET = 16;
	public static final int CLASS_TABLE_LENGTH_OFFSET = 20;
	public static final int CODE_ENTRY_POINT_OFFSET = 24;
	public static final int HEADER_LENGTH = 28;
}
