package aya;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class AyaStdIO {

	private PrintStream _out;
	private PrintStream _err;
	private InputStream _in;
	
	public AyaStdIO(PrintStream out, PrintStream err, InputStream in) {
		_out = out;
		_err = err;
		_in = in;
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
	}

	
}
