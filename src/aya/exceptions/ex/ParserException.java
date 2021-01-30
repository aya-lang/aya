package aya.exceptions.ex;

import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;

@SuppressWarnings("serial")
public class ParserException extends AyaException {
	
	protected ParserException(Symbol type, String msg) {
		super(type, msg);
	}

	public ParserException(String msg) {
		super(SymbolConstants.PARSER_ERR, msg);
	}
	
}
