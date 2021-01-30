package aya.exceptions.runtime;

import aya.obj.symbol.SymbolConstants;

@SuppressWarnings("serial")
public class MathError extends InternalAyaRuntimeException {

	public MathError(String message) {
		super(SymbolConstants.MATH_ERR, message);
	}

}
