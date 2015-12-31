package java.nio;

final class ArrayByteBuffer extends ByteBuffer {

	private final byte[] array;
	private final int array_offset;

	public ArrayByteBuffer(byte[] array, int offset, int length) {
		super(array.length);
		position = offset;
		limit = offset + length;
		array_offset = 0;
		this.array = array;
	}

}
