package aya.exceptions.ex;

import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;
import aya.parser.SourceStringRef;

@SuppressWarnings("serial")
public class ParserException extends AyaException {
	
	protected ParserException(Symbol type, String msg, SourceStringRef source) {
		super(type, msg + "\n" + source.getContextStr());
		this.setSource(source);
	}

	public ParserException(String msg, SourceStringRef source) {
		super(SymbolConstants.PARSER_ERR, msg + "\n" + source.getContextStr());
	}
	
}
