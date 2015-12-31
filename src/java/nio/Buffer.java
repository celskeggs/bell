package java.nio;

public abstract class Buffer {

	// invariant: 0 <= mark <= position <= limit <= capacity
	int mark, position, limit;
	final int capacity;
	// mark = -1 when undefined

	Buffer(int capacity) {
		position = 0;
		limit = capacity;
		mark = -1;
		this.capacity = capacity;
	}

	public final int capacity() {
		return capacity;
	}

	public final int position() {
		return position;
	}

	public final int limit() {
		return limit;
	}

	public final Buffer position(int newPosition) {
		if (newPosition < 0 || newPosition > limit) {
			throw new IllegalArgumentException();
		}
		if (mark > newPosition) {
			mark = -1;
		}
		position = newPosition;
		return this;
	}

	public final Buffer limit(int newLimit) {
		if (newLimit < 0 || newLimit > capacity) {
			throw new IllegalArgumentException();
		}
		if (mark > newLimit) {
			mark = -1;
		}
		if (position > newLimit) {
			position = newLimit;
		}
		limit = newLimit;
		return this;
	}

	public final Buffer mark() {
		mark = position;
		return this;
	}

	public final Buffer reset() {
		if (mark == -1) {
			throw new InvalidMarkException();
		}
		position = mark;
		return this;
	}

	public final Buffer clear() {
		position = 0;
		limit = capacity;
		mark = -1;
		return this;
	}

	public final Buffer flip() {
		limit = position;
		position = 0;
		mark = -1;
		return this;
	}

	public final Buffer rewind() {
		position = 0;
		mark = -1;
		return this;
	}

	public final int remaining() {
		return limit - position;
	}

	public final boolean hasRemaining() {
		return limit != position;
	}

	public abstract boolean isReadOnly();

	public abstract boolean hasArray();

	public abstract Object array();

	public abstract int arrayOffset();

	public abstract boolean isDirect();
}
