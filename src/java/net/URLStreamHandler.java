package java.net;

import java.io.IOException;

import com.celskeggs.bell.support.IncompleteImplementationError;

public abstract class URLStreamHandler {
	protected abstract URLConnection openConnection(URL u) throws IOException;

	protected void parseURL(URL u, String spec, int start, int limit) {
		throw new IncompleteImplementationError();
	}

	protected int getDefaultPort() {
		return -1;
	}

	protected boolean equals(URL u1, URL u2) {
		throw new IncompleteImplementationError();
	}

	protected int hashCode(URL u) {
		throw new IncompleteImplementationError();
	}

	protected boolean sameFile(URL u1, URL u2) {
		throw new IncompleteImplementationError();
	}

	protected Object /* TODO InetAddress */ getHostAddress(URL u) {
		throw new IncompleteImplementationError();
	}

	protected boolean hostsEqual(URL u1, URL u2) {
		throw new IncompleteImplementationError();
	}

	protected String toExternalForm(URL u) {
		throw new IncompleteImplementationError();
	}

	protected void setURL(URL u, String protocol, String host, int port, String authority, String userInfo, String path,
			String query, String ref) {
		throw new IncompleteImplementationError();
	}

	@Deprecated
	protected void setURL(URL u, String protocol, String host, int port, String file, String ref) {
		throw new IncompleteImplementationError();
	}
}
