package com.celskeggs.net.www;

public class ContentTypes {
	// TODO: implement some content types
	private ContentTypes() {
	}

	public static String getPackage() {
		String name = ContentTypes.class.getName();
		return name.substring(0, name.lastIndexOf('.'));
	}
}
