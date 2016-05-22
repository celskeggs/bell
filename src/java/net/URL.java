package java.net;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import com.celskeggs.net.protocols.Protocols;
import com.celskeggs.support.CUtil;

public final class URL /* implements Serializable */ {

	private static final HashMap<String, URLStreamHandler> handlers = new HashMap<String, URLStreamHandler>();
	// TODO: allow setting this up
	private static URLStreamHandlerFactory factory = null;
	private final URLStreamHandler handler;

	public static synchronized void setURLStreamHandlerFactory(URLStreamHandlerFactory fac) {
		if (factory != null) {
			throw new Error("Factory already set!");
		}
		factory = fac;
	}

	public URL(String protocol, String host, int port, String file) throws MalformedURLException {
		this(protocol, host, port, file, null);
	}

	public URL(String protocol, String host, String file) throws MalformedURLException {
		this(protocol, host, -1, file);
	}

	public URL(String protocol, String host, int port, String file, URLStreamHandler handler)
			throws MalformedURLException {
		if (handler == null) {
			handler = getHandler(protocol);
		}
		this.handler = handler;
	}

	public URL(String spec) throws MalformedURLException {
		this(null, spec);
	}

	public URL(URL context, String spec) throws MalformedURLException {
		this(context, spec, null);
	}

	public URL(URL context, String spec, URLStreamHandler handler) throws MalformedURLException {
		// TODO: use URLStreamHandler.parseURL?
		throw new IncompleteImplementationError();
	}

	private static synchronized URLStreamHandler getHandler(String protocol) throws MalformedURLException {
		URLStreamHandler handler = handlers.get(protocol);
		if (handler == null) {
			if (factory != null) {
				handler = factory.createURLStreamHandler(protocol);
			}
		}
		if (handler == null) {
			String prop = System.getProperty("java.protocol.handler.pkgs");
			if (prop != null) {
				String[] packages = CUtil.splitFixed(prop, '|');
				for (String pack : packages) {
					try {
						handler = Class.forName(pack + "." + protocol + ".Handler").asSubclass(URLStreamHandler.class)
								.newInstance();
						break;
					} catch (ClassNotFoundException e) {
						// continue on
					} catch (ClassCastException e) {
						// continue on
					} catch (InstantiationException e) {
						// continue on TODO: is this correct?
					} catch (IllegalAccessException e) {
						// continue on TODO: is this correct?
					}
				}
			}
		}
		if (handler == null) {
			try {
				handler = Class.forName(Protocols.getPackage() + "." + protocol + ".Handler")
						.asSubclass(URLStreamHandler.class).newInstance();
			} catch (ClassNotFoundException e) {
				throw new MalformedURLException("Cannot find protocol " + protocol);
			} catch (ClassCastException e) {
				// TODO: more detailed exceptions?
				throw new MalformedURLException("Cannot load protocol " + protocol);
			} catch (InstantiationException e) {
				throw new MalformedURLException("Cannot load protocol " + protocol);
			} catch (IllegalAccessException e) {
				throw new MalformedURLException("Cannot load protocol " + protocol);
			}
		}
		return handler;
	}

	public URLConnection openConnection() throws IOException {
		return handler.openConnection(this);
	}

	// TODO: with proxies

	public final InputStream openStream() throws IOException {
		return openConnection().getInputStream();
	}

	// TODO: do equals (and others) using URLStreamHandler.equals (and others)

	// TODO: the rest of the methods
}
