package element.parser.tokens;

import element.obj.Obj;
import element.obj.character.Char;

public class CharToken extends StdToken {
		
	public CharToken(String data) {
		super(data, Token.CHAR);
	}

	
	@Override
	public Obj getElementObj() {
		return Char.valueOf(data.charAt(0));
	}

	@Override
	public String typeString() {
		return "char";
	}
}
