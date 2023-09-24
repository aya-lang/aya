package aya.exceptions.ex;

import aya.obj.symbol.SymbolConstants;
import aya.parser.ParserString;

@SuppressWarnings("serial")
public class SyntaxError extends ParserException {
	
	public SyntaxError(String msg) {
		super(SymbolConstants.SYNTAX_ERR, msg);
	}
	
	public SyntaxError(String msg, ParserString in) {
		super(SymbolConstants.SYNTAX_ERR, msg + "\n" + in.getSource().getContextStr(in.currentIndex()));
	}
}
