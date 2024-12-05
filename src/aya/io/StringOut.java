package aya.io;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class StringOut {

	public String ENCODING = StandardCharsets.UTF_8.name();
	
	private ByteArrayOutputStream _out;
	private ByteArrayOutputStream _err;
	
	public StringOut() {
		_out = new ByteArrayOutputStream();
		_err = new ByteArrayOutputStream();
	}
	
	public ByteArrayOutputStream getOutStream() {
		return _out;
	}

	public ByteArrayOutputStream getErrStream() {
		return _out;
	}
	
	public String flushOut() {
		try {
			String s = _out.toString(ENCODING);
			_out.reset();
			return s;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return e.getLocalizedMessage();
		}
	}

	public String flushErr() {
		try {
			String s = _err.toString(ENCODING);
			_err.reset();
			return s;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return e.getLocalizedMessage();
		}
	}
}
