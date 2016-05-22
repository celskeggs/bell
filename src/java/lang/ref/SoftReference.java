package java.lang.ref;

public class SoftReference<T> extends Reference<T> {
	// TODO: actually make this SOFT
	private volatile T referent;

	public SoftReference(T referent) {
		this(referent, null);
	}

	public SoftReference(T referent, ReferenceQueue<? super T> q) {
		super(q);
		this.referent = referent;
	}

	public T get() {
		return referent;
	}

	@Override
	public void clear() {
		referent = null;
	}
}
