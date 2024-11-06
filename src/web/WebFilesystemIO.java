package web;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;

import aya.io.fs.AbstractFilesystemIO;

public class WebFilesystemIO extends AbstractFilesystemIO {

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

}
