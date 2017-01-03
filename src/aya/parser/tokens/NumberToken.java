package aya.parser.tokens;

import aya.obj.number.Num;
import aya.parser.SpecialNumberParser;

public class NumberToken extends StdToken {
	private boolean isSpecNum = false;
	
	public NumberToken(String data) {
		super(data, Token.NUMERIC);
	}

	public NumberToken(String data, boolean b) {
		super(data, Token.NUMERIC);
		isSpecNum = true;
	}

	@Override
	public Object getAyaObj() {
		if (isSpecNum) {
			return (new SpecialNumberParser(data)).toNumber();
		}
		else {
			return new Num(Double.parseDouble(data));
		}
	
	}

	@Override
	public String typeString() {
		return "num";
	}
}
