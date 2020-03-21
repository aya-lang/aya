package aya.exceptions;

@SuppressWarnings("serial")
public class AyaBuildException extends RuntimeException {
	String msg;
	
	public AyaBuildException(String msg) {
		super(msg);
		this.msg = msg;
	}
	
	public String getSimpleMessage() {
		return msg;
	}
}
