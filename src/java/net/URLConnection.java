package java.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.celskeggs.net.www.ContentTypes;

public abstract class URLConnection {
	protected URL url;
	protected boolean doInput = true;
	protected boolean doOutput = false;
	protected boolean allowUserInteraction = getDefaultAllowUserInteraction();
	protected boolean useCaches = getDefaultUseCaches();
	protected long ifModifiedSince = 0;
	protected boolean connected = false;
	private int connectTimeout, readTimeout;
	private static ContentHandlerFactory contentHandlerFactory;
	private static final HashMap<String, ContentHandler> cachedHandlers = new HashMap<String, ContentHandler>();
	private static boolean defaultAllowUserInteraction = false;
	private static boolean defaultUseCaches = false;
	private final HashMap<String, List<String>> requestProperties = new HashMap<String, List<String>>();

	protected URLConnection(URL url) {
		this.url = url;
	}

	// public static FileNameMap getFileNameMap() TODO
	// public static void setFileNameMap(FileNameMap map) TODO

	public abstract void connect() throws IOException;

	public void setConnectTimeout(int timeout) {
		if (timeout < 0) {
			throw new IllegalArgumentException("Negative timeout");
		}
		this.connectTimeout = timeout;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setReadTimeout(int timeout) {
		if (timeout < 0) {
			throw new IllegalArgumentException("Negative timeout");
		}
		this.readTimeout = timeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public URL getURL() {
		return url;
	}

	public long getHeaderFieldLong(String name, long default_) {
		String value = getHeaderField(name);
		if (value == null) {
			return default_;
		}
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException ex) {
			return default_;
		}
	}

	public int getHeaderFieldInt(String name, int default_) {
		String value = getHeaderField(name);
		if (value == null) {
			return default_;
		}
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException ex) {
			return default_;
		}
	}

	public long getHeaderFieldDate(String name, long default_) {
		throw new IncompleteImplementationError();
	}

	public int getContentLength() {
		return getHeaderFieldInt("content-length", -1);
	}

	public long getContentLengthLong() {
		return getHeaderFieldLong("content-length", -1);
	}

	public String getContentType() {
		return getHeaderField("content-type");
	}

	public String getContentEncoding() {
		return getHeaderField("content-encoding");
	}

	public long getExpiration() {
		return getHeaderFieldDate("expires", 0);
	}

	public long getDate() {
		return getHeaderFieldDate("date", 0);
	}

	public long getLastModified() {
		return getHeaderFieldDate("last-modified", 0);
	}

	public String getHeaderField(String name) {
		int i = 0;
		String key, value = null;
		do {
			key = getHeaderFieldKey(i);
			if (name.equals(key)) {
				value = getHeaderField(i);
			}
			i++;
		} while (key != null);
		return value;
	}

	public Map<String, List<String>> getHeaderFields() {
		HashMap<String, List<String>> out = new HashMap<String, List<String>>();
		String key;
		for (int i = 0; (key = getHeaderFieldKey(i)) != null; i++) {
			if (!out.containsKey(key)) {
				out.put(key, new ArrayList<String>());
			}
			out.get(key).add(getHeaderField(i++));
		}
		for (Map.Entry<String, List<String>> ent : out.entrySet()) {
			ent.setValue(Collections.unmodifiableList(ent.getValue()));
		}
		return Collections.unmodifiableMap(out);
	}

	public String getHeaderFieldKey(int n) {
		return null;
	}

	public String getHeaderField(int n) {
		return null;
	}

	private static synchronized ContentHandler getHandler(String contentType) throws UnknownServiceException {
		ContentHandler handler = cachedHandlers.get(contentType);
		if (handler == null) {
			if (contentHandlerFactory != null) {
				handler = contentHandlerFactory.createContentHandler(contentType);
			}
			if (handler == null) {
				Class<?> c;
				try {
					c = Class.forName(ContentTypes.getPackage() + "." + convertContentType(contentType));
					Object o = c.newInstance();
					if (o instanceof ContentHandler) {
						handler = (ContentHandler) o;
					} else {
						throw new UnknownServiceException("Unknown content type: " + contentType);
					}
				} catch (ClassNotFoundException e) {
					throw new UnknownServiceException("Unknown content type: " + contentType);
				} catch (InstantiationException e) {
					throw new UnknownServiceException("Unknown content type: " + contentType);
				} catch (IllegalAccessException e) {
					throw new UnknownServiceException("Unknown content type: " + contentType);
				}
			}
			cachedHandlers.put(contentType, handler);
		}
		return handler;
	}

	private static String convertContentType(String contentType) {
		char[] in = contentType.toCharArray();
		char[] out = new char[in.length];
		for (int i = 0; i < in.length; i++) {
			char c = in[i];
			if (c == '/') {
				c = '.';
			} else if (!Character.isLetter(c) && !Character.isDigit(c)) {
				c = '_';
			}
			out[i] = c;
		}
		return contentType.replace('/', '.');
	}

	public Object getContent() throws IOException {
		return getHandler(getContentType()).getContent(this);
	}

	public Object getContent(Class<?>[] classes) throws IOException {
		return getHandler(getContentType()).getContent(this, classes);
	}

	public InputStream getInputStream() throws IOException {
		throw new UnknownServiceException("Unreadable URL connection");
	}

	public OutputStream getOutputStream() throws IOException {
		throw new UnknownServiceException("Unwritable URL connection");
	}

	public String toString() {
		return "[Connection to " + url + ", " + (connected ? "connected" : "disconnected") + "]";
	}

	public void setDoInput(boolean doinput) {
		this.doInput = doinput;
	}

	public boolean getDoInput() {
		return doInput;
	}

	public void setDoOutput(boolean dooutput) {
		this.doOutput = dooutput;
	}

	public boolean getDoOutput() {
		return doOutput;
	}

	public void setAllowUserInteraction(boolean allowuserinteraction) {
		this.allowUserInteraction = allowuserinteraction;
	}

	public boolean getAllowUserInteraction() {
		return allowUserInteraction;
	}

	public static void setDefaultAllowUserInteraction(boolean defaultallowuserinteraction) {
		defaultAllowUserInteraction = defaultallowuserinteraction;
	}

	public static boolean getDefaultAllowUserInteraction() {
		return defaultAllowUserInteraction;
	}

	public void setUseCaches(boolean usecaches) {
		this.useCaches = usecaches;
	}

	public boolean getUseCaches() {
		return useCaches;
	}

	public void setIfModifiedSince(long ifmodifiedsince) {
		this.ifModifiedSince = ifmodifiedsince;
	}

	public long getIfModifiedSince() {
		return ifModifiedSince;
	}

	public boolean getDefaultUseCaches() {
		return defaultUseCaches;
	}

	public void setDefaultUseCaches(boolean defaultusecaches) {
		defaultUseCaches = defaultusecaches;
	}

	public void setRequestProperty(String key, String value) {
		if (connected) {
			throw new IllegalStateException("Already connected!");
		}
		if (key == null) {
			throw new NullPointerException();
		}
		List<String> values = requestProperties.get(key);
		if (values == null) {
			values = new ArrayList<String>();
			requestProperties.put(key, values);
		}
		values.clear();
		values.add(value);
	}

	public void addRequestProperty(String key, String value) {
		if (connected) {
			throw new IllegalStateException("Already connected!");
		}
		if (key == null) {
			throw new NullPointerException();
		}
		List<String> values = requestProperties.get(key);
		if (values == null) {
			values = new ArrayList<String>();
			requestProperties.put(key, values);
		}
		values.add(value);
	}

	public String getRequestProperty(String key) {
		if (connected) {
			throw new IllegalStateException("Already connected!");
		}
		List<String> values = requestProperties.get(key);
		return values == null ? null : values.size() == 0 ? null : values.get(values.size() - 1);
	}

	public Map<String, List<String>> getRequestProperties() {
		HashMap<String, List<String>> out = new HashMap<String, List<String>>();
		for (Map.Entry<String, List<String>> ent : requestProperties.entrySet()) {
			out.put(ent.getKey(), Collections.unmodifiableList(ent.getValue()));
		}
		return Collections.unmodifiableMap(out);
	}

	@Deprecated
	public static void setDefaultRequestProperty(String key, String value) {
		// Do nothing... because deprecated.
	}

	@Deprecated
	public static String getDefaultRequestProperty(String key) {
		return null;
	}

	public static synchronized void setContentHandlerFactory(ContentHandlerFactory fac) {
		if (contentHandlerFactory != null) {
			throw new Error("ContentHandlerFactory already set!");
		}
		contentHandlerFactory = fac;
	}

	public static String guessContentTypeFromName(String fname) {
		throw new IncompleteImplementationError();
	}

	public static String guessContentTypeFromStream(InputStream is) throws IOException {
		throw new IncompleteImplementationError();
	}
}
