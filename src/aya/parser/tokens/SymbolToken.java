package aya.parser.tokens;

import aya.Aya;
import aya.instruction.DataInstruction;
import aya.instruction.Instruction;
import aya.obj.symbol.Symbol;
import aya.parser.SourceStringRef;

public class SymbolToken extends StdToken {
		
	public SymbolToken(String data, SourceStringRef source) {
		super(data, Token.SYMBOL, source);
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
