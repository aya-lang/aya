package aya.parser.tokens;

import aya.Aya;
import aya.instruction.Instruction;
import aya.instruction.variable.GetCDictInstruction;
import aya.obj.symbol.Symbol;
import aya.parser.SourceStringRef;

public class CDictToken extends StdToken {

	public CDictToken(String data, SourceStringRef source) {
		super(data, Token.KEY_VAR, source);
	}
	
	public Symbol getSymbol() {
		return Aya.getInstance().getSymbols().getSymbol(data);
	}

	@Override
	public Instruction getInstruction() {
		return new GetCDictInstruction(getSymbol());
	}

	@Override
	public String typeString() {
		return "cdict";
	}
		
}
