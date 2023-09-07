package aya.exceptions.runtime;

import aya.obj.symbol.SymbolConstants;

/**
 * A Basic runtime error with a string message
 * @author npaul
 *
 */
@SuppressWarnings("serial")
public class AyaStackOverflowError extends InternalAyaRuntimeException {

	public AyaStackOverflowError(String msg) {
		super(SymbolConstants.VALUE_ERR, msg);
	}

}
