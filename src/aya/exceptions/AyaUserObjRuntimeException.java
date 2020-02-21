package aya.exceptions;

import aya.obj.Obj;

/**
 * A special runtime exception with a basic message. The exception is
 * caught by the interpreter and its message is printed.
 * @author Nick
 *
 */
@SuppressWarnings("serial")
public class AyaUserObjRuntimeException extends AyaRuntimeException {
	Obj obj;
	
	public AyaUserObjRuntimeException(Obj obj) {
		super(obj.str());
		this.obj = obj;
	}
	
	public String getSimpleMessage() {
		return obj.str();
	}

	public Obj getObj() {
		return obj;
	}
}
