package element.parser.tokens;

import java.math.BigDecimal;

public class NumberToken extends StdToken {
	public NumberToken(String data) {
		super(data, Token.NUM);
	}

	@Override
	public Object getElementObj() {
		try {
			return Integer.parseInt(data);
		} catch (NumberFormatException e) {
			return new BigDecimal(data);
		}
	}

	@Override
	public String typeString() {
		return "num";
	}
}
