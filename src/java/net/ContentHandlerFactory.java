package java.net;

public interface ContentHandlerFactory {
	public ContentHandler createContentHandler(String mimetype);
}
