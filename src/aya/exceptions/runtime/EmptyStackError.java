package aya.exceptions.runtime;

import aya.obj.symbol.SymbolConstants;

@SuppressWarnings("serial")
public class EmptyStackError extends InternalAyaRuntimeException {

	public EmptyStackError(String msg) {
		super(SymbolConstants.EMPTY_STACK_ERR, msg);
	}

}
