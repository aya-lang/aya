package aya.exceptions;

@SuppressWarnings("serial")
public class SyntaxError extends AyaBuildException {
	
	public SyntaxError(String msg) {
		super("Syntax Error: " + msg);
	}
}
