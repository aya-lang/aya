package aya.exceptions;


/**
 * A special runtime exception with a basic message. The exception is
 * caught by the interpreter and its message is printed.
 * @author Nick
 *
 */
@SuppressWarnings("serial")
public class AyaRuntimeException extends RuntimeException {
	String msg;
	
	public AyaRuntimeException(String msg) {
		super(msg);
		this.msg = msg;
	}
	
	public String getSimpleMessage() {
		return msg;
	}
}
