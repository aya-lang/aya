package element.parser.tokens;

public class BoolToken extends StdToken {
		
	public BoolToken(String data) {
		super(data, Token.BOOL);
	}

	
	@Override
	public Object getElementObj() {
		return data.charAt(0) == 'T';
	}

	@Override
	public String typeString() {
		return "bool";
	}
}
