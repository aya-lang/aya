package aya.exceptions.ex;

import aya.obj.symbol.SymbolConstants;
import aya.parser.SourceStringRef;

@SuppressWarnings("serial")
public class EndOfInputError extends ParserException {
	
	public EndOfInputError(String msg, SourceStringRef source) {
		super(SymbolConstants.END_OF_INPUT_ERR, msg, source);
	}

}
