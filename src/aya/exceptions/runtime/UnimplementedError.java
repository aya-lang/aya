package aya.exceptions.runtime;

import aya.obj.symbol.SymbolConstants;

/**
 * A Basic runtime error with a string message
 * @author npaul
 *
 */
@SuppressWarnings("serial")
public class UnimplementedError extends InternalAyaRuntimeException {

	public UnimplementedError(String msg) {
		super(SymbolConstants.UNIMPL_ERROR, msg);
	}

	public UnimplementedError() {
		super(SymbolConstants.UNIMPL_ERROR, "Method not implemented");
	}

}
