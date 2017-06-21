package aya;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;

import aya.obj.Obj;
import aya.obj.character.Char;
import aya.obj.list.Str;
import aya.obj.number.Num;

public class AyaPrefs {
	private static String prompt = "aya> ";
	private static String workingDir = null;
	private static String defaultWorkingDir = null;
	
	protected static final String BUG_MESSAGE =  "An unhandled exception occurred. If this is a bug, please submit an issue to "
			+ "https://github.com/nick-paul/aya-lang/issues with the stacktrace below.\n"
			+ "=== [ Stacktrace ] ===";
	

	public static void init() {
		initWorkingDir();
	}
	
	private static void initWorkingDir() {
		try {
			workingDir = Aya.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
//			if(workingDir.length() > 0) {
//				workingDir = workingDir.substring(1, workingDir.length()); //Remove the leading '/'
//			}
			if(workingDir.contains(".jar")) {
				int ix = workingDir.lastIndexOf('/');
				workingDir = workingDir.substring(0, ix+1);
			}
		} catch (URISyntaxException e) {
			workingDir = "";
			Aya.getInstance().printDebug("Cannot locate working dir");
		}
		defaultWorkingDir = workingDir;
	}

	public static String getPrompt() {
		return prompt;
	}

	public static void setPrompt(String prompt) {
		AyaPrefs.prompt = prompt;
	}

	public static String getWorkingDir() {
		return workingDir;
	}

	public static boolean setWorkingDir(String workingDir) {
		//Make sure path is a directory
		char last = workingDir.charAt(workingDir.length()-1);
		if (last != '/' && last != '\\') {
			return false;
		}
		try {
			//Create a path to test if it exists
			Path path = new File(workingDir).toPath();
			if (Files.exists(path)) {
				AyaPrefs.workingDir = workingDir;
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	public static ArrayList<String> listFilesForWorkingDir() {
		return listFilesForFolder(new File(getWorkingDir()));
	}
	
	public static ArrayList<String> listFilesForFolder(final File folder) {
		File[] listOfFiles = folder.listFiles();
		ArrayList<String> fileList = new ArrayList<String>();
		for (File file : listOfFiles) {
		    if (file.isFile()) {
		        fileList.add(file.getName());
		    } 
		}
		return fileList;
	}
	
	
	public static ArrayList<String> listFilesAndDirsForFolder(final File folder) {
		File[] listOfFiles = folder.listFiles();
		ArrayList<String> fileList = new ArrayList<String>();
		for (File file : listOfFiles) {
		    if (file.isFile()) {
		        fileList.add(file.getName());
		    } else if (file.isDirectory()) {
		    	fileList.add(File.separator + file.getName());
		    }
		}
		return fileList;
	}
	
	public static boolean mkDir(String dirName) {
		File theDir = new File(dirName);

		// if the directory does not exist, create it
		if (!theDir.exists()) {
		    try{
		        theDir.mkdir();
		        return true;
		    } 
		    catch(SecurityException se){
		        return false;
		    }        
		}
		return true;
	}
	
	public static boolean deleteFile(String filename) {
		Path path = new File(filename).toPath();
		try {
		    Files.delete(path);
		} catch (NoSuchFileException x) {
			return false;
		} catch (DirectoryNotEmptyException x) {
			return false;
		} catch (IOException x) {
			return false;
		}
		return true;
	}
	
	public static void resetWorkingDir() {
		workingDir = defaultWorkingDir;
	}
	
	public static final Str FILE_SEPARATOR = new Str(File.separator);
	public static final Str FILE_PATH_SEPARATOR = new Str(File.pathSeparator);
	public static final Str SYS_LINE_SEPARATOR = new Str(System.lineSeparator());
	
	
	public static final Obj[] CONSTS = {
			/* 00 */ Num.PI,
			/* 01 */ Num.E,
			/* 02 */ Num.DOUBLE_MAX,
			/* 03 */ Num.DOUBLE_MIN,
			/* 04 */ Num.DOUBLE_NAN ,
			/* 05 */ Num.DOUBLE_INF,
			/* 06 */ Num.DOUBLE_NINF,
			/* 07 */ Num.INT_MAX,
			/* 08 */ Num.INT_MIN,
			/* 09 */ FILE_SEPARATOR,
			/* 10 */ FILE_PATH_SEPARATOR,
			/* 11 */ Char.MAX_VALUE,
			/* 12 */ SYS_LINE_SEPARATOR
	};
	
}
