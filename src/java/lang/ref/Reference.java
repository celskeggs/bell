package java.lang.ref;

// TODO: implement this better
public abstract class Reference<T> {
	boolean enqueued;
	final ReferenceQueue<? super T> queue;

	Reference(ReferenceQueue<? super T> queue) {
		this.queue = queue;
	}

	public abstract T get();

	public abstract void clear();

	public synchronized boolean isEnqueued() {
		return enqueued;
	}

	public synchronized boolean enqueue() {
		if (queue != null && !enqueued) {
			enqueued = true;
			queue.enqueue(this);
		}
		return false;
	}
}
