package aya.exceptions;

@SuppressWarnings("serial")
public class AyaException extends Exception {
	String msg;
	
	public AyaException(String msg) {
		super(msg);
		this.msg = msg;
	}
	
	public String getSimpleMessage() {
		return msg;
	}
}
