package aya.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import aya.AyaPrefs;
import aya.StaticData;

public class FileUtils {

	public static String readAllText(File file) throws IOException {
		return new String(readAllBytes(file), StandardCharsets.UTF_8); // in Java 11 you can also do Files.readString(Path)
	}
	
	public static byte[] readAllBytes(File file) throws IOException { 
		return StaticData.FILESYSTEM.readAllBytes(file);
	}

	/**
	 * Resolves a pathName to a File, either relative to the current {@link AyaPrefs#getWorkingDir()} or absolute.
	 * <p> Absolute pathNames are handled based on the Operating System
	 */
	public static File resolveFile(String pathName) {
		File file = new File(pathName);
		return file.isAbsolute() ? file : new File(AyaPrefs.getWorkingDir(), pathName);
	}
	
	/** See resolveFile(String) */
	public static Path resolvePath(String pathName) {
		Path path = Path.of(pathName);
		return path.isAbsolute() ? path : Path.of(AyaPrefs.getWorkingDir(), pathName);
		
	}

	public static boolean isFile(String str) {
		return StaticData.FILESYSTEM.isFile(resolveFile(str));
	}
	
	public static String resolveHome(String path) {
		return path.replaceFirst("^~", System.getProperty("user.home"));
	}
	
}
