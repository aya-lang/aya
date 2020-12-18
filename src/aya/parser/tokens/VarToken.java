package aya.parser.tokens;

import aya.instruction.Instruction;
import aya.instruction.variable.GetVariableInstruction;
import aya.obj.symbol.SymbolEncoder;

public class VarToken extends StdToken {
		
	public VarToken(String data) {
		super(data, Token.VAR);
	}
	
	public long id() {
		return SymbolEncoder.encodeString(data);
	}

	@Override
	public Instruction getInstruction() {
		return new GetVariableInstruction(id());
	}

	@Override
	public String typeString() {
		return "var";
	}
}
