package element.parser.tokens;

import element.obj.dict.KeyVariable;

public class KeyVarToken extends StdToken {

	public KeyVarToken(String data) {
		super(data, Token.MEM_VAR);
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
