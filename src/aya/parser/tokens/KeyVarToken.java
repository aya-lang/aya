package aya.parser.tokens;

import aya.Aya;
import aya.instruction.Instruction;
import aya.instruction.variable.GetKeyVariableInstruction;
import aya.obj.symbol.Symbol;

public class KeyVarToken extends StdToken {

	public KeyVarToken(String data) {
		super(data, Token.KEY_VAR);
	}
	
	public Symbol getSymbol() {
		return Aya.getInstance().getSymbols().getSymbol(data);
	}

	@Override
	public Instruction getInstruction() {
		return new GetKeyVariableInstruction(getSymbol());
	}

	@Override
	public String typeString() {
		return "keyvar";
	}
		
}
