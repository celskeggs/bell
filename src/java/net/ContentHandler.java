package java.net;

public abstract class ContentHandler {
	public abstract Object getContent(URLConnection urlc);

	public Object getContent(URLConnection urlc, Class[] classes) {
		Object value = getContent(urlc);
		for (Class<?> c : classes) {
			if (c.isInstance(value)) {
				return value;
			}
		}
		return value;
	}
}
