package aya.parser.tokens;

import aya.exceptions.parser.EndOfInputError;
import aya.exceptions.parser.SyntaxError;
import aya.instruction.Instruction;
import aya.instruction.variable.GetKeyVariableInstruction;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolTable;
import aya.parser.SourceStringRef;

public class KeyVarToken extends EscapedToken {

	public KeyVarToken(String data, SourceStringRef source, boolean hasExplicitTerminator) throws SyntaxError, EndOfInputError {
		super(data, Token.KEY_VAR, source, hasExplicitTerminator);
	}
	
	public Symbol getSymbol() {
		return SymbolTable.getSymbol(unescapedData);
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
