package aya.exceptions.runtime;

import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;

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

	@Override
	public Dict getDict() {
		Dict d = super.getDict();
		d.set(SymbolConstants.DATA, obj);
		return d;
	}

	@Override
	public Symbol typeSymbol() {
		return Obj.IDToSym(obj.type());
	}
}
