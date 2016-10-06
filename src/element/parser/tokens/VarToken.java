package element.parser.tokens;

import element.variable.Variable;

public class VarToken extends StdToken {
		
	public VarToken(String data) {
		super(data, Token.VAR);
	}

	@Override
	public Object getElementObj() {
		return new Variable(data);
	}

	@Override
	public String typeString() {
		return "var";
	}
}
