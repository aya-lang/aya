package aya.parser.tokens;

import aya.instruction.Instruction;
import aya.instruction.variable.GetVariableInstruction;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolTable;
import aya.parser.SourceStringRef;

public class VarToken extends StdToken {
		
	public VarToken(String data, SourceStringRef source) {
		super(data, Token.VAR, source);
	}
	
	public Symbol getSymbol() {
		return SymbolTable.getSymbol(data);
	}

	@Override
	public Instruction getInstruction() {
		return new GetVariableInstruction(this.getSourceStringRef(), getSymbol());
	}

	@Override
	public String typeString() {
		return "var";
	}
}
