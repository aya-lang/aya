package aya.parser.tokens;

import aya.obj.Obj;
import aya.obj.character.Char;

public class CharToken extends StdToken {
		
	public CharToken(String data) {
		super(data, Token.CHAR);
	}

	
	@Override
	public Obj getAyaObj() {
		return Char.valueOf(data.charAt(0));
	}

	@Override
	public String typeString() {
		return "char";
	}
}
