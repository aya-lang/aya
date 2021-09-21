package aya.parser.tokens;

import aya.Aya;
import aya.instruction.Instruction;
import aya.instruction.variable.GetCDictInstruction;
import aya.obj.symbol.Symbol;

public class CDictToken extends StdToken {

	public CDictToken(String data) {
		super(data, Token.KEY_VAR);
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
