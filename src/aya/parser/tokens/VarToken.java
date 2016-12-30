package aya.parser.tokens;

import aya.variable.Variable;

public class VarToken extends StdToken {
		
	public VarToken(String data) {
		super(data, Token.VAR);
	}

	@Override
	public Object getAyaObj() {
		return new Variable(data);
	}

	@Override
	public String typeString() {
		return "var";
	}
}
