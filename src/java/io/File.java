package java.io;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import com.celskeggs.bell.support.FileSystem;
import com.celskeggs.bell.support.IncompleteImplementationError;
import com.celskeggs.bell.support.FileSystem.Attributes;

public class File implements Comparable<File> /* , Serializable */ {

	public static final char separatorChar = System.getProperty("file.separator").charAt(0);
	public static final String separator = String.valueOf(separatorChar);
	public static final char pathSeparatorChar = System.getProperty("path.separator").charAt(0);
	public static final String pathSeparator = String.valueOf(pathSeparatorChar);

	private final boolean isAbsolute;
	private final String[] elems;
	private File parent, absolute, canonical;
	private boolean delete_on_exit;

	public File(String pathname) {
		this((File) null, pathname);
	}

	private static String[] toElements(String name) {
		if (name.isEmpty()) {
			return new String[0];
		} else {
			int count = 0;
			int i = name.indexOf('/');
			while (i != -1) {
				int ni = name.indexOf(separatorChar, i + 1);
				if (ni != i + 1) {
					count++;
				}
				i = ni;
			}
			String[] elems = new String[count];
			count = 0;
			i = -1;
			do {
				int ni = name.indexOf(separatorChar, i + 1);
				if (ni != i + 1) {
					elems[count++] = name.substring(i + 1, ni);
				}
				i = ni;
			} while (i != -1);
			return elems;
		}
	}

	public File(String parent, String child) {
		this(parent == null ? null : new File(parent), child);
	}

	public File(File parent, String child) {
		if (parent == null) {
			isAbsolute = child.startsWith("/");
			// TODO: confirm that toElements drops a leading slash
			elems = toElements(child);
		} else if (parent.elems.length == 0 && !parent.isAbsolute) {
			isAbsolute = true;
			elems = toElements(child);
		} else {
			String[] child_elements = toElements(child);
			if (child_elements.length == 0) {
				this.parent = parent.parent;
				this.isAbsolute = parent.isAbsolute;
				this.elems = parent.elems;
			} else {
				elems = Arrays.copyOf(parent.elems, parent.elems.length + child_elements.length);
				System.arraycopy(child_elements, 0, elems, parent.elems.length, child_elements.length);
				isAbsolute = parent.isAbsolute;
				if (child_elements.length == 1) {
					this.parent = parent;
				}
			}
		}
		if (isAbsolute) {
			absolute = this;
		}
	}

	private File(boolean isAbsolute, String[] elems) {
		this.isAbsolute = isAbsolute;
		this.elems = elems;
	}

	public File(URI uri) {
		throw new IncompleteImplementationError();
	}

	public String getName() {
		return elems.length == 0 ? "" : elems[elems.length - 1];
	}

	public String getParent() {
		File f = getParentFile();
		if (f == null) {
			return null;
		} else {
			return f.getPath();
		}
	}

	public File getParentFile() {
		if (parent != null) {
			return parent;
		} else if (elems.length == 0) {
			return null;
		} else {
			return parent = new File(isAbsolute, Arrays.copyOf(elems, elems.length - 1));
		}
	}

	public String getPath() {
		if (elems.length == 0) {
			return isAbsolute ? "/" : "";
		}
		StringBuilder sb = new StringBuilder(isAbsolute ? "/" : "").append(elems[0]);
		for (int i = 1; i < elems.length; i++) {
			sb.append('/').append(elems[i]);
		}
		return sb.toString();
	}

	public boolean isAbsolute() {
		return isAbsolute;
	}

	public File getAbsoluteFile() {
		if (absolute != null) {
			return absolute;
		} else if (isAbsolute) {
			return absolute = this;
		} else {
			return absolute = new File(System.getProperty("user.dir"), this.getPath());
		}
	}

	public String getAbsolutePath() {
		return getAbsoluteFile().getPath();
	}

	public File getCanonicalFile() throws IOException {
		if (canonical != null) {
			return canonical;
		}
		if (!isAbsolute) {
			return canonical = getAbsoluteFile().getCanonicalFile();
		}
		ArrayList<String> elems = new ArrayList<String>();
		boolean mix = false;
		for (String elem : this.elems) {
			if (elem.equals("..")) {
				if (!elems.isEmpty()) {
					// "/../../../../x" -> "/x"
					elems.remove(elems.size() - 1);
				}
				mix = true;
			} else if (elem.equals(".")) {
				mix = true;
				// drop it
			} else {
				elems.add(elem);
			}
		}
		if (mix) {
			canonical = new File(true, elems.toArray(new String[elems.size()]));
			canonical.canonical = canonical;
			return canonical;
		} else {
			// no actual changes from the original...
			return canonical = this;
		}
	}

	public String getCanonicalPath() throws IOException {
		return getCanonicalFile().getPath();
	}

	@Deprecated
	public URL toURL() throws MalformedURLException {
		return toURI().toURL();
	}

	public URI toURI() {
		throw new IncompleteImplementationError();
	}

	public boolean canRead() {
		Attributes a = FileSystem.attributes(this);
		return a != null && a.readable;
	}

	public boolean canWrite() {
		Attributes a = FileSystem.attributes(this);
		return a != null && a.writable;
	}

	public boolean canExecute() {
		Attributes a = FileSystem.attributes(this);
		return a != null && a.executable;
	}

	public boolean exists() {
		return FileSystem.attributes(this) != null;
	}

	public boolean isDirectory() {
		Attributes a = FileSystem.attributes(this);
		return a != null && a.directory;
	}

	public boolean isFile() {
		Attributes a = FileSystem.attributes(this);
		return a != null && a.file;
	}

	public boolean isHidden() {
		Attributes a = FileSystem.attributes(this);
		return a != null && a.hidden;
	}

	public long lastModified() {
		Attributes a = FileSystem.attributes(this);
		return a == null ? 0L : a.lastModified;
	}

	public long length() {
		Attributes a = FileSystem.attributes(this);
		return a == null ? 0L : a.length;
	}

	public boolean createNewFile() throws IOException {
		return FileSystem.atomicNewFile(this);
	}

	public boolean delete() {
		try {
			FileSystem.delete(this);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public synchronized void deleteOnExit() {
		if (!delete_on_exit) {
			delete_on_exit = true;
			throw new IncompleteImplementationError();
		}
	}

	public String[] list() {
		try {
			return FileSystem.contents(this);
		} catch (IOException e) {
			return null;
		}
	}

	public String[] list(FilenameFilter filter) {
		String[] base = list();
		if (base == null) {
			return null;
		}
		if (filter == null) {
			return base;
		}
		ArrayList<String> files = new ArrayList<String>();
		for (String name : base) {
			if (filter.accept(this, name)) {
				files.add(name);
			}
		}
		return files.toArray(new String[files.size()]);
	}

	public File[] listFiles() {
		String[] base = list();
		File[] out = new File[base.length];
		for (int i = 0; i < base.length; i++) {
			out[i] = new File(this, base[i]);
		}
		return out;
	}

	public File[] listFiles(FilenameFilter filter) {
		String[] base = list(filter);
		File[] out = new File[base.length];
		for (int i = 0; i < base.length; i++) {
			out[i] = new File(this, base[i]);
		}
		return out;
	}

	public File[] listFiles(FileFilter filter) {
		File[] base = listFiles();
		if (base == null) {
			return null;
		}
		if (filter == null) {
			return base;
		}
		ArrayList<File> files = new ArrayList<File>();
		for (File file : base) {
			if (filter.accept(file)) {
				files.add(file);
			}
		}
		return files.toArray(new File[files.size()]);
	}

	public boolean mkdir() {
		try {
			FileSystem.mkdir(this);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public boolean mkdirs() {
		File parent = getAbsoluteFile().getParentFile();
		if (parent != null) {
			if (!parent.isDirectory() && !parent.mkdirs()) {
				return false;
			}
		}
		return this.mkdir();
	}

	public boolean renameTo(File dest) {
		if (dest == null) {
			throw new NullPointerException();
		}
		try {
			FileSystem.rename(this, dest);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public boolean setLastModified(long time) {
		try {
			FileSystem.setLastModified(this, time);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public boolean setReadOnly() {
		try {
			FileSystem.setReadOnly(this);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public boolean setWritable(boolean writable) {
		return setWritable(writable, true);
	}

	public boolean setWritable(boolean writable, boolean ownerOnly) {
		try {
			FileSystem.setWritable(this, writable, ownerOnly);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public boolean setReadable(boolean readable) {
		return setReadable(readable, true);
	}

	public boolean setReadable(boolean readable, boolean ownerOnly) {
		try {
			FileSystem.setReadable(this, readable, ownerOnly);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public boolean setExecutable(boolean executable) {
		return setExecutable(executable, true);
	}

	public boolean setExecutable(boolean executable, boolean ownerOnly) {
		try {
			FileSystem.setExecutable(this, executable, ownerOnly);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	private static final File filesystem_root = new File("/");

	public static File[] listRoots() {
		return new File[] { filesystem_root };
	}

	public long getTotalSpace() {
		try {
			return FileSystem.fsAttributes(this).total_space;
		} catch (IOException e) {
			return 0;
		}
	}

	public long getFreeSpace() {
		try {
			return FileSystem.fsAttributes(this).free_space;
		} catch (IOException e) {
			return 0;
		}
	}

	public long getUsableSpace() {
		try {
			return FileSystem.fsAttributes(this).usable_space;
		} catch (IOException e) {
			return 0;
		}
	}

	private static long next_temp = 0;

	private static synchronized long getTemp() {
		if (next_temp < 0) {
			// in rare cases, we might break the "only once" contract, but it's
			// better than crashing
			next_temp = 0;
		}
		return next_temp++;
	}

	public static File createTempFile(String prefix, String suffix, File directory) throws IOException {
		if (prefix.length() < 3) {
			throw new IllegalArgumentException();
		}
		if (suffix == null) {
			suffix = ".tmp";
		}
		if (directory == null) {
			directory = new File(System.getProperty("java.io.tmpdir"));
		}
		while (true) {
			File target = new File(directory, prefix + Long.toHexString(getTemp()) + suffix);
			if (target.exists()) {
				continue;
			}
			if (target.createNewFile()) {
				return target;
			}
		}
	}

	public static File createTempFile(String prefix, String suffix) throws IOException {
		return createTempFile(prefix, suffix, null);
	}

	public int compareTo(File pathname) {
		return getPath().compareTo(pathname.getPath());
	}

	public boolean equals(Object obj) {
		if (obj instanceof File) {
			File f = (File) obj;
			return isAbsolute == f.isAbsolute && Arrays.equals(elems, f.elems);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return getPath().hashCode() ^ 1234321;
	}

	public String toString() {
		return getPath();
	}

	// public Path toPath() {} TODO
}
