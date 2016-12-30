package aya.exceptions;

@SuppressWarnings("serial")
public class EndOfInputError extends RuntimeException {
	String msg;
	
	public EndOfInputError(String msg) {
		super(msg);
		this.msg = msg;
	}
	
	public String getSimpleMessage() {
		return msg;
	}
}
