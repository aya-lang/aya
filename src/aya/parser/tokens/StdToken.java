package aya.parser.tokens;

public abstract class StdToken extends Token {
	protected StdToken(String data, int type) {
		super(type);
		this.data = data;
	}
}
