package element.parser.tokens;

public class CharToken extends StdToken {
		
	public CharToken(String data) {
		super(data, Token.CHAR);
	}

	
	@Override
	public Object getElementObj() {
		return data.charAt(0);
	}

	@Override
	public String typeString() {
		return "char";
	}
}
