package aya.parser.tokens;

import aya.instruction.Instruction;
import aya.instruction.variable.GetCDictInstruction;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolTable;
import aya.parser.SourceStringRef;

public class CDictToken extends StdToken {

	public CDictToken(String data, SourceStringRef source) {
		super(data, Token.KEY_VAR, source);
	}
	
	public Symbol getSymbol() {
		return SymbolTable.getSymbol(data);
	}

	@Override
	public Instruction getInstruction() {
		return new GetCDictInstruction(getSourceStringRef(), getSymbol());
	}

	@Override
	public String typeString() {
		return "cdict";
	}
		
}
