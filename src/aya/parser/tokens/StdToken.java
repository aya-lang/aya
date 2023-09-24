package aya.parser.tokens;

import aya.parser.SourceStringRef;

public abstract class StdToken extends Token {
	protected StdToken(String data, int type, SourceStringRef source) {
		super(type, source);
		this.data = data;
	}
}
