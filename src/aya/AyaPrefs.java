package aya;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import aya.exceptions.runtime.IOError;
import aya.obj.Obj;
import aya.obj.character.Char;
import aya.obj.list.Str;
import aya.obj.number.Num;
import aya.util.FileUtils;

public class AyaPrefs {
	private static String prompt = "aya> ";
	private static String workingDir = null;
	private static String defaultWorkingDir = null;
	private static String[] args = null;
	private static boolean typeCheckerEnabled = true;
	
	protected static final String BUG_MESSAGE =  "An unhandled exception occurred. If this is a bug, please submit an issue to "
			+ "https://github.com/nick-paul/aya-lang/issues with the stacktrace below.\n"
			+ "=== [ Stacktrace ] ===";

	public static File getAyaRootDirectory() {
		try {
			File codeSource = new File(AyaThread.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			if(codeSource.isFile()) {
				codeSource = codeSource.getParentFile();
			}
			return codeSource;
		} catch (URISyntaxException e) {
			StaticData.IO.printDebug("Cannot locate aya dir: " + e.getMessage());
			return null;
		}
	}

	public static void initDefaultWorkingDir() {
		File rootDir = getAyaRootDirectory();
		workingDir = rootDir == null ? "" : (rootDir.getAbsolutePath() + File.separator);
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
	
	private static String getAbsPath(String dir) {
		//Make sure path is a directory
		char last = dir.charAt(dir.length()-1);
		if (last != '/' && last != '\\') {
			dir += File.separator;
		}
		try {
			File fwd = FileUtils.resolveFile(dir);
			if (fwd.exists()) {
				return fwd.getCanonicalPath() + File.separator;
			} else {
				//System.out.println("setWorkingDir: error, dir does not exist: " + workingDir);
				return null;
			}
		} catch (Exception e) {
			//System.out.println("setWorkingDir: error: " + e.getMessage());
			return null;
		}
	}
	
	public static String getAyaDir() {
		return defaultWorkingDir;
	}

	public static boolean setWorkingDir(String workingDir) {
		String abs_path = getAbsPath(workingDir);
		if (abs_path != null) {
			AyaPrefs.workingDir = abs_path;
			return true;
		} else {
			return false;
		}
	}

	public static boolean setAyaDir(String dir) {
		String abs_path = getAbsPath(dir);
		if (abs_path != null) {
			AyaPrefs.defaultWorkingDir = abs_path;
			return true;
		} else {
			return false;
		}
	}

	public static ArrayList<String> listFilesAndDirsForFolder(final Path path) {
		File[] listOfFiles = path.toFile().listFiles();
		ArrayList<String> fileList = new ArrayList<String>();
		if (listOfFiles == null) {
			throw new IOError("AyaPrefs.listFilesAndDirsForFolder", path.toString(), "Unable to list files, path is invalid");
		} else {
			for (File file : listOfFiles) {
			    if (file.isFile()) {
			        fileList.add(file.getName());
			    } else if (file.isDirectory()) {
			    	fileList.add(file.getName() + File.separator);
			    }
			}
		}
		return fileList;
	}
	
	public static boolean mkDir(String dirName) {
		File theDir = FileUtils.resolveFile(dirName);

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
	
	public static boolean deleteFile(File file) {
		try {
		    Files.delete(file.toPath());
		} catch (IOException x) {
			return false;
		}
		return true;
	}
	
	public static void resetWorkingDir() {
		workingDir = defaultWorkingDir;
	}
	

	
	
	public static String CONSTANTS_HELP = "constants follow the format :Nc where N is:\n"
			+ "   0: pi\n"
			+ "   1: e\n"
			+ "   2: double max\n"
			+ "   3: double min\n"
			+ "   4: nan\n"
			+ "   5: inf\n"
			+ "   6: -inf\n"
			+ "   7: int max\n"
			+ "   8: int min\n"
			+ "   9: char max\n";

	
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
			/* 09 */ Char.MAX_VALUE,
	};

	public static final String SYS_HOME_DIR = System.getProperty("user.home");
	public static final Str SYS_HOME_DIR_STR = new Str(SYS_HOME_DIR);

	public static String getHomeDir() {
		return SYS_HOME_DIR;
	}
	public static Str getHomeDirStr() {
		return SYS_HOME_DIR_STR;
	}
	
	public static void setArgs(String[] args) {
		AyaPrefs.args = args;
	}
	
	public static String[] getArgs() {
		return args;
	}
	
	/** This should only ever be set once when Aya starts up
	 * It changes the way instructions are generated and should not
	 * be changed after parsing any code
	 * @param x
	 */
	public static void setTypeCheckerEnabled(boolean x) {
		typeCheckerEnabled = x;
	}
	
	public static boolean isTypeCheckerEnabled() {
		return typeCheckerEnabled;
	}
	

}
