package element.util;

@SuppressWarnings("serial")
public class SimpleException extends Exception {
	String msg;
	
	public SimpleException(String msg) {
		super(msg);
		this.msg = msg;
	}
	
	public String getSimpleMessage() {
		return msg;
	}
}
