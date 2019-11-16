package aya.parser.tokens;

import aya.instruction.DataInstruction;
import aya.instruction.Instruction;
import aya.obj.symbol.Symbol;

public class SymbolToken extends StdToken {
		
	public SymbolToken(String data) {
		super(data, Token.SYMBOL);
	}
	
	public long getID() {
		return Symbol.fromStr(data).id();
	}

	
	@Override
	public Instruction getInstruction() {
		return new DataInstruction(Symbol.fromStr(data));
	}

	@Override
	public String typeString() {
		return "symbol";
	}
}
