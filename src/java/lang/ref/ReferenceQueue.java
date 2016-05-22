package java.lang.ref;

import java.util.LinkedList;

public class ReferenceQueue<T> {
	// TODO: address memory concerns with using a linked list
	private final LinkedList<Reference<? extends T>> entries = new LinkedList<Reference<? extends T>>();

	void enqueue(Reference<? extends T> ref) {
		synchronized (entries) {
			entries.addLast(ref);
		}
	}

	public Reference<? extends T> poll() {
		synchronized (entries) {
			return entries.pollFirst();
		}
	}

	public Reference<? extends T> remove(long timeout) throws IllegalArgumentException, InterruptedException {
		synchronized (entries) {
			while (entries.isEmpty()) {
				entries.wait(timeout);
			}
			return entries.removeFirst();
		}
	}

	public Reference<? extends T> remove() throws InterruptedException {
		synchronized (entries) {
			while (entries.isEmpty()) {
				entries.wait();
			}
			return entries.removeFirst();
		}
	}
}
