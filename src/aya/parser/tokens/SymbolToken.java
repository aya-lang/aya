package aya.parser.tokens;

import aya.Aya;
import aya.instruction.DataInstruction;
import aya.instruction.Instruction;
import aya.obj.symbol.Symbol;

public class SymbolToken extends StdToken {
		
	public SymbolToken(String data) {
		super(data, Token.SYMBOL);
	}
	
	public Symbol getSymbol() {
		return Aya.getInstance().getSymbols().getSymbol(data);
	}

	
	@Override
	public Instruction getInstruction() {
		return new DataInstruction(getSymbol());
	}

	@Override
	public String typeString() {
		return "symbol";
	}
}
