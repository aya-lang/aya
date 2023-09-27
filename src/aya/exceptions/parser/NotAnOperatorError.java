package aya.exceptions.parser;

import aya.obj.symbol.SymbolConstants;
import aya.parser.SourceStringRef;

@SuppressWarnings("serial")
public class NotAnOperatorError extends ParserException {

	public NotAnOperatorError(String operator, SourceStringRef source) {
		super(SymbolConstants.NOT_AN_OP_ERROR, operator + " is not a valid operator", source);
	}

}
