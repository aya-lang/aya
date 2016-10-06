package element.exceptions;

@SuppressWarnings("serial")
public class SyntaxError extends RuntimeException {
	String msg;
	
	public SyntaxError(String msg) {
		super(msg);
		this.msg = msg;
	}
	
	public String getSimpleMessage() {
		return msg;
	}
}
