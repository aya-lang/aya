package aya.exceptions.runtime;

import aya.obj.symbol.SymbolConstants;

/**
 * This error is thrown when a reference is invalid. For example, file streams, sockets, and graphics windows
 * @author npaul
 *
 */
@SuppressWarnings("serial")
public class InvalidReferenceError extends InternalAyaRuntimeException {

	public InvalidReferenceError(String operator_name, int reference) {
		super(SymbolConstants.INVALID_REF_ERR,
				"Invalid reference at " + operator_name + ": Reference " + reference + " does not exist");
	}

}
