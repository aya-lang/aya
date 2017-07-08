package aya.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import aya.obj.Obj;

public class FileUtils {
	
	@SuppressWarnings("null")
	public static String readAllText(String path) throws IOException {
		File file = new File(path);
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			String line;
			br = new BufferedReader(new FileReader(file));
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
	
}
