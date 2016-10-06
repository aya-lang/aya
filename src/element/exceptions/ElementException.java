package element.exceptions;

@SuppressWarnings("serial")
public class ElementException extends Exception {
	String msg;
	
	public ElementException(String msg) {
		super(msg);
		this.msg = msg;
	}
	
	public String getSimpleMessage() {
		return msg;
	}
}
