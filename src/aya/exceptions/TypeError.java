package aya.exceptions;

import aya.entities.Operation;
import aya.obj.Obj;

@SuppressWarnings("serial")
public class TypeError extends RuntimeException {
	String msg;
	
	public String getSimpleMessage() {
		return msg;
	}
	
	public TypeError(String opname, String expected, Obj... recieved) {
		super("Type error at (" + opname + "):\n\tExpected (" + expected + ")\n\tRecieved (" + listStr(recieved));
		msg = "Type error at (" + opname + "):\n\tExpected (" + expected + ")\n\tRecieved (" + listStr(recieved);
	}
	
	public TypeError(Operation op, Obj... recieved) {
		super("Type error at (" + op.name + "):\n\tExpected (" + op.argTypes + ")\n\tRecieved (" + listStr(recieved));
		msg = "Type error at (" + op.name + "):\n\tExpected (" + op.argTypes + ")\n\tRecieved (" + listStr(recieved);
	}
	
	public static String listStr(Obj[] recieved) {
		String msg = "";
		for(int i = recieved.length-1; i>=0; i--) {
			msg += recieved[i].repr() + " ";
		}
		return msg  + ")";
	}
}
