package aya.exceptions.ex;

import aya.obj.symbol.Symbol;

@SuppressWarnings("serial")
public abstract class AyaException extends Exception {
	String msg;
	Symbol type;
	
	public AyaException(Symbol type, String msg) {
		super(msg);
		this.msg = msg;
		this.type = type;
	}
	
	public String getSimpleMessage() {
		return msg;
	}
	
	public Symbol typeSymbol() {
		return type;
	}
}
