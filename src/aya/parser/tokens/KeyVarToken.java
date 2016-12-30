package aya.parser.tokens;

import aya.obj.dict.KeyVariable;

public class KeyVarToken extends StdToken {

	public KeyVarToken(String data) {
		super(data, Token.KEY_VAR);
	}

	@Override
	public Object getElementObj() {
		return new KeyVariable(data);
	}

	@Override
	public String typeString() {
		return "keyvar";
	}
		
}
