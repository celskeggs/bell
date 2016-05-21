package java.lang.reflect;

public class Modifier {
	public static final int ABSTRACT = 1024;
	public static final int FINAL = 16;
	public static final int INTERFACE = 512;
	public static final int NATIVE = 256;
	public static final int PRIVATE = 2;
	public static final int PROTECTED = 4;
	public static final int PUBLIC = 1;
	public static final int STATIC = 8;
	public static final int STRICT = 2048;
	public static final int SYNCHRONIZED = 32;
	public static final int TRANSIENT = 128;
	public static final int VOLATILE = 64;

	public static int classModifiers() {
		return PUBLIC | FINAL | INTERFACE | ABSTRACT | STATIC | FINAL | STRICT;
	}

	public static int interfaceModifiers() {
		return PUBLIC | FINAL | INTERFACE | ABSTRACT | STATIC | STRICT;
	}

	public static int constructorModifiers() {
		return PUBLIC | PRIVATE | PROTECTED;
	}

	public static int fieldModifiers() {
		return PUBLIC | PRIVATE | PROTECTED | STATIC | FINAL | TRANSIENT | VOLATILE;
	}

	public static int methodModifiers() {
		return PUBLIC | PRIVATE | PROTECTED | STATIC | FINAL | SYNCHRONIZED | NATIVE | STRICT | ABSTRACT;
	}

	public static int parameterModifiers() {
		return FINAL;
	}

	public static boolean isAbstract(int mod) {
		return (mod & ABSTRACT) != 0;
	}

	public static boolean isFinal(int mod) {
		return (mod & FINAL) != 0;
	}

	public static boolean isInterface(int mod) {
		return (mod & INTERFACE) != 0;
	}

	public static boolean isNative(int mod) {
		return (mod & NATIVE) != 0;
	}

	public static boolean isPrivate(int mod) {
		return (mod & PRIVATE) != 0;
	}

	public static boolean isProtected(int mod) {
		return (mod & PROTECTED) != 0;
	}

	public static boolean isPublic(int mod) {
		return (mod & PUBLIC) != 0;
	}

	public static boolean isStatic(int mod) {
		return (mod & STATIC) != 0;
	}

	public static boolean isStrict(int mod) {
		return (mod & STRICT) != 0;
	}

	public static boolean isSynchronized(int mod) {
		return (mod & SYNCHRONIZED) != 0;
	}

	public static boolean isTransient(int mod) {
		return (mod & TRANSIENT) != 0;
	}

	public static boolean isVolatile(int mod) {
		return (mod & VOLATILE) != 0;
	}

	public static String toString(int mod) {
		if (mod == 0) {
			return "";
		}
		// public protected private abstract static final transient volatile synchronized native strictfp interface 
		StringBuilder sb = new StringBuilder();
		if ((mod & PUBLIC) != 0) {
			sb.append("public ");
		}
		if ((mod & PROTECTED) != 0) {
			sb.append("protected ");
		}
		if ((mod & PRIVATE) != 0) {
			sb.append("private ");
		}
		if ((mod & ABSTRACT) != 0) {
			sb.append("abstract ");
		}
		if ((mod & STATIC) != 0) {
			sb.append("static ");
		}
		if ((mod & FINAL) != 0) {
			sb.append("final ");
		}
		if ((mod & TRANSIENT) != 0) {
			sb.append("transient ");
		}
		if ((mod & VOLATILE) != 0) {
			sb.append("volatile ");
		}
		if ((mod & SYNCHRONIZED) != 0) {
			sb.append("synchronized ");
		}
		if ((mod & NATIVE) != 0) {
			sb.append("native ");
		}
		if ((mod & STRICT) != 0) {
			sb.append("strictfp ");
		}
		if ((mod & INTERFACE) != 0) {
			sb.append("interface ");
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}
}
