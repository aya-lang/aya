package aya.exceptions.runtime;

import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;

/**
 * A special runtime exception with a basic message. The exception is
 * caught by the interpreter and its message is printed.
 * @author Nick
 *
 */
@SuppressWarnings("serial")
public class UndefVarException extends InternalAyaRuntimeException {
	
	public UndefVarException(Symbol var) {
		super(SymbolConstants.UNDEF_VAR, "Undefined variable '" + var.name() +"'");
	}
	
}
