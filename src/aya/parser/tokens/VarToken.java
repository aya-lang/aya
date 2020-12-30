package aya.parser.tokens;

import aya.Aya;
import aya.instruction.Instruction;
import aya.instruction.variable.GetVariableInstruction;
import aya.obj.symbol.Symbol;

public class VarToken extends StdToken {
		
	public VarToken(String data) {
		super(data, Token.VAR);
	}
	
	public Symbol getSymbol() {
		return Aya.getInstance().getSymbols().getSymbol(data);
	}

	@Override
	public Instruction getInstruction() {
		return new GetVariableInstruction(getSymbol());
	}

	@Override
	public String typeString() {
		return "var";
	}
}
