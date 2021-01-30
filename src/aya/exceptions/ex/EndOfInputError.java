package aya.exceptions.ex;

import aya.obj.symbol.SymbolConstants;

@SuppressWarnings("serial")
public class EndOfInputError extends ParserException {
	
	public EndOfInputError(String msg) {
		super(SymbolConstants.END_OF_INPUT_ERR, msg);
	}

}
