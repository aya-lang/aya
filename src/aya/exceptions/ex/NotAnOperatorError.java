package aya.exceptions.ex;

import aya.obj.symbol.SymbolConstants;

@SuppressWarnings("serial")
public class NotAnOperatorError extends ParserException {

	public NotAnOperatorError(String operator) {
		super(SymbolConstants.NOT_AN_OP_ERROR, operator + " is not a valid operator");
	}

}
