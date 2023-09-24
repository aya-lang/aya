package aya.exceptions.ex;

import aya.obj.symbol.SymbolConstants;
import aya.parser.ParserString;

@SuppressWarnings("serial")
public class SyntaxError extends ParserException {
	
	public SyntaxError(String msg) {
		super(SymbolConstants.SYNTAX_ERR, "Syntax Error: " + msg);
	}
	
	public SyntaxError(String msg, ParserString in) {
		super(SymbolConstants.SYNTAX_ERR, "Syntax Error: " + msg + "\n" + in.getSource().getContextStr(in.currentIndex()));
	}
}
