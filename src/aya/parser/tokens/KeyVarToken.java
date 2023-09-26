package aya.parser.tokens;

import aya.Aya;
import aya.instruction.Instruction;
import aya.instruction.variable.GetKeyVariableInstruction;
import aya.obj.symbol.Symbol;
import aya.parser.SourceStringRef;

public class KeyVarToken extends StdToken {

	public KeyVarToken(String data, SourceStringRef source) {
		super(data, Token.KEY_VAR, source);
	}
	
	public Symbol getSymbol() {
		return Aya.getInstance().getSymbols().getSymbol(data);
	}

	@Override
	public Instruction getInstruction() {
		return new GetKeyVariableInstruction(getSourceStringRef(), getSymbol());
	}

	@Override
	public String typeString() {
		return "keyvar";
	}
		
}
