package aya.exceptions.runtime;

import aya.obj.symbol.Symbol;

@SuppressWarnings("serial")
public class InternalAyaRuntimeException extends AyaRuntimeException {

	Symbol type;
	
	public InternalAyaRuntimeException(Symbol type, Exception ex) {
		super(ex);
		this.type = type;
	}

	public InternalAyaRuntimeException(Symbol type,  String e) {
		super(e);
		this.type = type;
	}
	
	@Override
	public Symbol typeSymbol() {
		return type;
	}
}
