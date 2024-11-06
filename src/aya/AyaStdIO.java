package aya;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import aya.io.stdin.InputWrapper;

public class AyaStdIO {
	private PrintStream _out;
	private PrintStream _err;
	private InputStream _in;
	private InputWrapper _input_wrapper;
	
	public AyaStdIO(PrintStream out, PrintStream err, InputStream in, InputWrapper iw) {
		_out = out;
		_err = err;
		setIn(in, iw);
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
	
	public String nextLine() {
		return _input_wrapper.nextLine();
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
	
	public void setIn(InputStream is, InputWrapper input_wrapper) {
		_in = is;
		_input_wrapper = input_wrapper;
	}

	public InputWrapper inputWrapper() {
		return _input_wrapper;
	}

	
}
