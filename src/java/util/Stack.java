package java.util;

public class Stack<E> extends Vector<E> {
	public E push(E item) {
		addElement(item);
		return item;
	}

	public synchronized E pop() {
		if (elementCount > 0) {
			return (E) elementData[--elementCount];
		} else {
			throw new EmptyStackException();
		}
	}

	public synchronized E peek() {
		if (elementCount > 0) {
			return (E) elementData[elementCount - 1];
		} else {
			throw new EmptyStackException();
		}
	}

	public synchronized boolean empty() {
		return elementCount == 0;
	}

	public synchronized int search(Object o) {
		for (int i = elementCount - 1; i >= 0; i--) {
			if (Objects.equals(o, elementData[i])) {
				return elementCount - i;
			}
		}
		return -1;
	}
}
