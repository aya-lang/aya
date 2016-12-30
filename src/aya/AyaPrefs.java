package aya;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;

public class AyaPrefs {
	private static String prompt = "element> ";
	private static String workingDir = null;
	private static String defaultWorkingDir = null;

	

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
			Aya.getInstance().getOut().printWarn("Cannot locate working dir");
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
	
	
}
