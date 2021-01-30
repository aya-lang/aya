package aya.exceptions.runtime;

import aya.instruction.named.NamedInstruction;
import aya.instruction.op.OpInstruction;
import aya.obj.Obj;
import aya.obj.symbol.SymbolConstants;

@SuppressWarnings("serial")
public class TypeError extends InternalAyaRuntimeException {
	String msg;
	
	public String getSimpleMessage() {
		return msg;
	}
	
	public TypeError(OpInstruction op, Obj... recieved) {
		super(SymbolConstants.TYPE_ERR, "Type error at (" + op.name + "):\n\tExpected (" 
				+ op.getDocTypeStr()
				+ ")\n\tReceived (" + listStr(recieved));
		
		msg = "Type error at (" + op.name + "):\n\tExpected (" 
				+ op.getDocTypeStr()
				+ ")\n\tReceived (" + listStr(recieved);
	}

	public TypeError(OpInstruction op, String message, Obj... recieved) {
		super(SymbolConstants.TYPE_ERR, "Type error at (" + op.name + "):\n\t" + message + "\n\tExpected (" 
				+ op.getDocTypeStr()
				+ ")\n\tReceived (" + listStr(recieved));
		
		msg = "Type error at (" + op.name + "):\n\tExpected (" 
				+ op.getDocTypeStr()
				+ ")\n\tReceived (" + listStr(recieved);
	}
	
	public TypeError(NamedInstruction inst, String expected, Obj... recieved) {
		super(SymbolConstants.TYPE_ERR,
				"Type error at " + inst.getName() + "\n\tExpected (" 
				+ expected
				+ ")\n\tReceived (" + listStr(recieved));
		
		msg = "Type error at (" + inst.getName() + ") \n\tExpected (" 
				+ expected
				+ ")\n\tReceived (" + listStr(recieved);
	}
	
	public TypeError(String s) {
		super(SymbolConstants.TYPE_ERR, s);
		msg = s;
	}
	


	public static String listStr(Obj[] recieved) {
		String msg = "";
		for(int i = recieved.length-1; i>=0; i--) {
			msg += recieved[i].repr() + " ";
		}
		return msg  + ")";
	}
}
