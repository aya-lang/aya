package aya;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class AyaStdIO {

	private PrintStream _out;
	private PrintStream _err;
	private InputStream _in;
	private Scanner _scanner; 
	
	public AyaStdIO(PrintStream out, PrintStream err, InputStream in) {
		_out = out;
		_err = err;
		setIn(in);
	}
	
	public PrintStream out() {
		return _out;
	}

	public PrintStream err() {
		return _err;
	}

	public InputStream in() {
		return _in;
	}
	
	public Scanner scanner() {
		return _scanner;
	}
	
	public String nextLine() {
		return _scanner.nextLine();
	}

	/** Return true if input is ready to be read */
	public boolean isInputAvaiable() {
		try {
			return _in.available() > 0;
		} catch (IOException e) {
			return false;
		}
	}
	
	public void printDebug(Object o) {
		if (StaticData.DEBUG) {
			_out.println(o.toString());
		}
	}
	
	public void setOut(OutputStream os) {
		try {
			_out = new PrintStream(os, true, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
	}
	
	public void setErr(OutputStream os) {
		try {
			_err = new PrintStream(os, true, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
	}
	
	public void setIn(InputStream is) {
		_in = is;
		_scanner = new Scanner(_in, "UTF-8");
	}

	
}
