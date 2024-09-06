package aya.util;

import aya.AyaPrefs;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FileUtils {

	@SuppressWarnings("null")
	public static String readAllText(File file) throws IOException {
		return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8); // in Java 11 you can also do Files.readString(Path)
	}

	/**
	 * Resolves a pathName to a File, either relative to the current {@link AyaPrefs#getWorkingDir()} or absolute.
	 * <p> Absolute pathNames are handled based on the Operating System
	 */
	public static File resolveFile(String pathName) {
		File file = new File(pathName);
		return file.isAbsolute() ? file : new File(AyaPrefs.getWorkingDir(), pathName);
	}

	public static boolean isFile(String str) {
		return resolveFile(str).isFile();
	}
	
	public static String resolveHome(String path) {
		return path.replaceFirst("^~", System.getProperty("user.home"));
	}
	
}
