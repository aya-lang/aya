package element.parser.tokens;

import element.obj.number.Num;

public class NumberToken extends StdToken {
	public NumberToken(String data) {
		super(data, Token.NUMERIC);
	}

	@Override
	public Object getElementObj() {
		return new Num(Double.parseDouble(data));
	
	}

	@Override
	public String typeString() {
		return "num";
	}
}
