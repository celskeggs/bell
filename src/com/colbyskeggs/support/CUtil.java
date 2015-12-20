package com.colbyskeggs.support;

import java.lang.reflect.Array;

public class CUtil {

	public static <T> T[] copyOfType(T[] suppressed, int i) {
		return (T[]) Array.newInstance(suppressed.getClass().getComponentType(), i);
	}

}
