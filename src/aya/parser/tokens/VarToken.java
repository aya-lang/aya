package aya.parser.tokens;

import aya.instruction.variable.GetVariableInstruction;
import aya.variable.Variable;

public class VarToken extends StdToken {
		
	public VarToken(String data) {
		super(data, Token.VAR);
	}
	
	public long getID() {
		return Variable.encodeString(data);
	}

	@Override
	public Object getAyaObj() {
		return new GetVariableInstruction(getID());
	}

	@Override
	public String typeString() {
		return "var";
	}
}
