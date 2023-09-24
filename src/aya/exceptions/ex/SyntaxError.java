package aya.exceptions.ex;

import aya.obj.symbol.SymbolConstants;
import aya.parser.SourceStringRef;

@SuppressWarnings("serial")
public class SyntaxError extends ParserException {
	
	public SyntaxError(String msg) {
		super(SymbolConstants.SYNTAX_ERR, msg);
	}
	
	public SyntaxError(String msg, SourceStringRef source) {
		super(SymbolConstants.SYNTAX_ERR, msg + "\n" + source.getContextStr());
	}
}
