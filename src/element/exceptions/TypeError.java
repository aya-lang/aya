package element.exceptions;

import element.ElemTypes;
import element.entities.Operation;

@SuppressWarnings("serial")
public class TypeError extends RuntimeException {
	String msg;
	
	public String getSimpleMessage() {
		return msg;
	}
	
	public TypeError(String opname, String expected, Object... recieved) {
		super("Type error at (" + opname + "):\n\tExpected (" + expected + ")\n\tRecieved (" + listStr(recieved));
		msg = "Type error at (" + opname + "):\n\tExpected (" + expected + ")\n\tRecieved (" + listStr(recieved);
	}
	
	public TypeError(Operation op, Object... recieved) {
		super("Type error at (" + op.name + "):\n\tExpected (" + op.argTypes + ")\n\tRecieved (" + listStr(recieved));
		msg = "Type error at (" + op.name + "):\n\tExpected (" + op.argTypes + ")\n\tRecieved (" + listStr(recieved);
	}
	
	public static String listStr(Object[] recieved) {
		String msg = "";
		for(int i = recieved.length-1; i>=0; i--) {
			msg += ElemTypes.debugString(recieved[i]) + " ";
		}
		return msg  + ")";
	}
}
