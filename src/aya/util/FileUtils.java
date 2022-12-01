package aya.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import aya.AyaPrefs;

public class FileUtils {
	
	@SuppressWarnings("null")
	public static String readAllText(String path) throws IOException {
		File file = new File(path);
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			String line;
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
			while((line = br.readLine()) != null) {
				sb.append(line+'\n');
			}
		} catch (IOException e) {
			try {
				br.close();
			} catch (NullPointerException e2) {}
			throw e;
		}
		
		br.close();
		return sb.toString();
	}

	public static String pathAppend(String dir1, String dir2) {
		return dir1 + File.separator + dir2;
	}
	
	public static String workingRelative(String dir) {
		if (dir.startsWith("/") || dir.startsWith("C:")) {
			return dir;
		} else {
			return pathAppend(AyaPrefs.getWorkingDir(), dir);
		}
	}

	public static boolean isFile(String str) {
		return new File(str).isFile();
	}
	
	public static String resolveHome(String path) {
		return path.replaceFirst("^~", System.getProperty("user.home"));
	}
	
}
