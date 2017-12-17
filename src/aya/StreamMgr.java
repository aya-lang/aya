package aya;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.stream.Collectors;

public class StreamMgr {
	
	public static int NO_FILE = 0;
	
	public static int INFO_INPUT = 1;
	public static int INFO_OUTPUT = 2;
	
	private static HashMap<Integer, BufferedReader> _input_streams = new HashMap<Integer, BufferedReader>();
	private static HashMap<Integer, PrintWriter> _output_streams = new HashMap<Integer, PrintWriter>();
	private static int _currentIndex = 10;
	
	public static final int STDIN = 1;
	public static final int STDOUT = 2;
	public static final int STDERR = 3;
	
	/////////////////////////
	// OPENING AND CLOSING //
	/////////////////////////
	
	public static int open(String filename, String type) {
		switch (type) {
		case "r":
			return open_r(filename);
		case "w":
			// Write: overwrite
			return open_w(filename, 'w');
		case "a":
			// Write: append
			return open_w(filename, 'a');
		default:
			return NO_FILE;
		}
	}
	
	private static int open_r(String filename) {
		BufferedReader f = null;
		boolean valid = false;
		
		try {
			File file = new File(filename);
			FileReader fr = new FileReader(file);
			f = new BufferedReader(fr);
			valid = true;
		} catch (IOException e) {
			valid = false;
		}
		
		if (valid) {
			_currentIndex++;
			_input_streams.put(_currentIndex, f);
			return _currentIndex;
		} else {
			return NO_FILE;
		}
	}
	
	
	private static int open_w(String filename, char type) {
		PrintWriter pw = null;
		boolean valid = false;
		
		try {
			File file = new File(filename);
			FileOutputStream fos = new FileOutputStream(file, type == 'a');
			pw = new PrintWriter(fos);
			valid = true;
		} catch (IOException e) {
			valid = false;
		}
		
		if (valid) {
			_currentIndex++;
			_output_streams.put(_currentIndex, pw);
			return _currentIndex;
		} else {
			return NO_FILE;
		}
	}
	
	
	/** Return true if the stream is closed, false if the file doesnt exist */
	public static boolean close(int fileid) {
		boolean success = false;
		if (_input_streams.containsKey(fileid)) {
			try {
				_input_streams.get(fileid).close();
				success = true;
			} catch (IOException e) {
				success = false;
			} finally {
				_input_streams.remove(fileid);
			}
		} else if (_output_streams.containsKey(fileid)) {
			_output_streams.get(fileid).close();
			_output_streams.remove(fileid);
			success = true;
		}
		
		return success;
	}
	
	
	/////////////
	// READING //
	/////////////
	
	/** Read the next line in the stream
	 *  Returns null if invalid file or at end of stream */
	public static String readline(int fileid) {
		BufferedReader f = _input_streams.get(fileid);
		if (f == null) return null;
		try {
			return f.readLine();
		} catch (IOException e) {
			return null;
		}
	}
	
	/** Read the next character in the stream as an int
	 *  Returns -1 if invalid file or at end of stream */
	public static int read(int fileid) {
		BufferedReader f = _input_streams.get(fileid);
		if (f == null) return -1;
		try {
			return f.read();
		} catch (IOException e) {
			return -1;
		}
	}
	
	public static String readAll(String path) throws IOException {
		File file = new File(path);
		String output = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			output = br.lines().collect(Collectors.joining("\n"));
			br.close();
		} catch (IOException e) {
			br.close();
			output = null;
		}
		
		return output;
	}
	
	public static String readAll(int fileid) {
		BufferedReader f = _input_streams.get(fileid);
		if (f == null) return null;
		return f.lines().collect(Collectors.joining("\n"));
	}
	
	
	/////////////
	// WRITING //
	/////////////
	
	public static boolean print(int fileid, String str) {
		switch (fileid) {
		case STDOUT:
			Aya.getInstance().getOut().print(str);
			return true;
		case STDIN:
			Aya.getInstance().getErr().print(str);
			return true;
		default:
			return print_f(fileid, str);
		}
	}
	
	
	public static boolean println(int fileid, String str) {
		switch (fileid) {
		case STDOUT:
			Aya.getInstance().getOut().println(str);
			return true;
		case STDIN:
			Aya.getInstance().getErr().println(str);
			return true;
		default:
			return println_f(fileid, str);
		}
	}
	
	
	private static boolean print_f(int fileid, String str) {
		PrintWriter pw = _output_streams.get(fileid);
		if (pw != null) {
			pw.print(str);
			return true;
		} else {
			return false;
		}
	}
	
	private static boolean println_f(int fileid, String str) {
		PrintWriter pw = _output_streams.get(fileid);
		if (pw != null) {
			pw.println(str);
			return true;
		} else {
			return false;
		}
	}

	/** Flush the output stream. True if stream exists */
	public static boolean flush(int fileid) {
		PrintWriter pw = _output_streams.get(fileid);
		if (pw != null) {
			pw.flush();
			return true;
		} else {
			return false;
		}
	}
	
	/** Stream info:
	 * 0: No stream or closed
	 * 1: input
	 * 2: output
	 * @param fileid
	 * @return
	 */
	public static int info(int fileid) {
		if (_input_streams.containsKey(fileid)) return INFO_INPUT;
		if (_output_streams.containsKey(fileid)) return INFO_OUTPUT;
		return NO_FILE;
	}
	
}
