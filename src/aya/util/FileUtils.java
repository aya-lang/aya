package aya.util;

import java.io.File;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.nio.file.StandardCopyOption;

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

	/**
	 * @return the File extension of the file | or null if no extension is present
	 */
	public static String getExt(File file) {
		String name = file.getName();
		int sepIdx = name.lastIndexOf('.');
		return sepIdx >= 0 ? name.substring(sepIdx + 1) : null;
	}

	// Unzip a file into a directory
	public static void unzip(File zipFile, Path destinationPath) throws IOException {
		// Create input stream for the zip file
		try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile))) {
			ZipEntry entry;

			// Iterate through each entry in the zip file
			while ((entry = zipInputStream.getNextEntry()) != null) {
				Path destFile = destinationPath.resolve(entry.getName());

				// Create directories if needed
				if (entry.isDirectory()) {
					Files.createDirectories(destFile);
				} else {
					// Create parent directories for the file if they don't exist
					Path parentDir = destFile.getParent();
					if (parentDir != null && !Files.exists(parentDir)) {
						Files.createDirectories(parentDir);
					}

					// Write the file content
					try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFile.toFile()))) {
						byte[] buffer = new byte[1024];
						int bytesRead;
						while ((bytesRead = zipInputStream.read(buffer)) != -1) {
							bos.write(buffer, 0, bytesRead);
						}
					}
				}
				zipInputStream.closeEntry();
			}
		}
	}

	public static void moveDir(Path src, Path dst) throws IOException {
		// Check if the source exists and is a directory
		if (Files.exists(src) && Files.isDirectory(src)) {
			// Perform the move or rename operation
			Files.move(src, dst, StandardCopyOption.REPLACE_EXISTING);
		} else {
			throw new IOException("Source directory does not exist or is not a directory.");
		}
	}
}
