package aya.exceptions.runtime;

import aya.obj.Obj;
import aya.obj.symbol.Symbol;

/**
 * A special runtime exception with a basic message. The exception is
 * caught by the interpreter and its message is printed.
 * @author Nick
 *
 */
@SuppressWarnings("serial")
public class UserObjRuntimeException extends AyaRuntimeException {
	Obj obj;
	
	public UserObjRuntimeException(Obj obj) {
		super(obj.str());
		this.obj = obj;
	}
	
	public String getSimpleMessage() {
		return obj.str();
	}

	public Obj getObj() {
		return obj;
	}

	@Override
	public Symbol typeSymbol() {
		return Obj.IDToSym(obj.type());
	}
}
