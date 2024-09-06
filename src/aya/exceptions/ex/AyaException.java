package aya.exceptions.ex;

import aya.obj.symbol.Symbol;
import aya.parser.SourceStringRef;

@SuppressWarnings("serial")
public abstract class AyaException extends Exception implements AyaExceptionInterface {
	String msg;
	Symbol type;
	private SourceStringRef source;
	
	public AyaException(Symbol type, String msg, SourceStringRef source) {
		super(msg);
		this.msg = msg;
		this.type = type;
		this.source = source;
	}

	public AyaException(Symbol type, String msg) {
		this(type, msg, null);
	}
	
	public String getSimpleMessage() {
		return msg;
	}
	
	public Symbol typeSymbol() {
		return type;
	}

	public void setSource(SourceStringRef source) {
		this.source = source;
	}
	
	public SourceStringRef getSource() {
		return this.source;
	}
}
