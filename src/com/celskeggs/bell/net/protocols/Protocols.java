package com.celskeggs.bell.net.protocols;

public final class Protocols {
	// TODO: implement http, https, file, and jar protocols.
	private Protocols() {
	}

	public static String getPackage() {
		String name = Protocols.class.getName();
		return name.substring(0, name.lastIndexOf('.'));
	}
}
