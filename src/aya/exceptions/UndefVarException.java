package aya.exceptions;


/**
 * A special runtime exception with a basic message. The exception is
 * caught by the interpreter and its message is printed.
 * @author Nick
 *
 */
@SuppressWarnings("serial")
public class UndefVarException extends ElementRuntimeException {
	
	public UndefVarException(String msg) {
		super(msg);
		this.msg = msg;
	}
	
	public String getSimpleMessage() {
		return msg;
	}
}
