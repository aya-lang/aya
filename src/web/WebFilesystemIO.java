package web;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;

import aya.io.fs.AbstractFilesystemIO;

public class WebFilesystemIO extends AbstractFilesystemIO {
	/**
	 * A very basic virtual filesystem implementation
	 * This is primarily used to load the standard library in the web implementation
	 * 
	 * Very few features outside of loading the standard library are supported
	 */

	private static class FileData {
		public byte[] data;

		public FileData(byte[] bytes) {
			data = bytes;
		}

		public static FileData fromString(String s) {
			return new FileData(s.getBytes(StandardCharsets.UTF_8));
		}
	}

	private HashMap<String, FileData> _files;

	public WebFilesystemIO() {
		_files = new HashMap<String, FileData>();
	}

	public void addFile(String path, String content) {
		_files.put(toPath(new File(path)), FileData.fromString(content));
	}

	private static String toPath(File f) {
		String p = f.getAbsolutePath();
		if (!p.startsWith("/")) {
			p = "/" + p;
		}
		return p;
	}

	@Override
	public byte[] readAllBytes(File file) throws IOException {
		FileData data = _files.get(toPath(file));
		if (data == null) {
			throw new IOException();
		} else {
			return data.data;
		}
	}

	@Override
	public void write(File file, byte[] bytes, StandardOpenOption create, StandardOpenOption truncateExisting)
			throws IOException {
		// TODO: open option and truncate option
		_files.put(toPath(file), new FileData(bytes));
	}

	public ArrayList<String> listFiles() {
		ArrayList<String> out = new ArrayList<String>();
		for (String s : _files.keySet()) {
			out.add(s);
		}
		return out;
	}

	@Override
	public boolean isFile(File file) {
		String path = toPath(file);
		String[] sections = path.split("/");
		String basename = sections[sections.length-1];
		return basename.contains(".") && _files.get(path) != null;
	}
	
	// Implementation of normalize using only string functions
	public static String normalizePath(String path) {
		if (path == null || path.isEmpty()) {
			return "";
		}
		
		// Split the path by "/"
		String[] parts = path.split("/");
		StringBuilder normalized = new StringBuilder();
		int skip = 0;
		
		// Traverse the path components in reverse
		for (int i = parts.length - 1; i >= 0; i--) {
			String part = parts[i];
		
			// Ignore empty parts and current directory "."
			if (part.isEmpty() || part.equals(".")) {
				continue;
			}
		
			// Handle parent directory ".."
			if (part.equals("..")) {
				skip++;
			} else {
				if (skip > 0) {
					skip--; // Skip this directory as it's canceled out by a ".."
				} else {
					normalized.insert(0, part).insert(0, "/");
				}
			}
		}
		
		// Handle root cases
		String result = normalized.length() > 0 ? normalized.toString() : "/";
		return result;
	}
	
	
	public static String joinPaths(String basePath, String relativePath) {
		if (basePath == null || basePath.isEmpty()) {
			return relativePath == null ? "" : relativePath;
		}
		if (relativePath == null || relativePath.isEmpty()) {
			return basePath;
		}
		
		// Ensure basePath does not end with a trailing slash
		if (basePath.endsWith("/")) {
			basePath = basePath.substring(0, basePath.length() - 1);
		}
		
		// Ensure relativePath does not start with a leading slash
		if (relativePath.startsWith("/")) {
			relativePath = relativePath.substring(1);
		}
		
		// Combine the paths with a single slash in between
		return basePath + "/" + relativePath;
	}


}
