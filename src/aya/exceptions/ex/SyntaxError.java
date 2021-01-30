package aya.exceptions.ex;

import aya.obj.symbol.SymbolConstants;

@SuppressWarnings("serial")
public class SyntaxError extends ParserException {
	
	public SyntaxError(String msg) {
		super(SymbolConstants.SYNTAX_ERR, "Syntax Error: " + msg);
	}
}
