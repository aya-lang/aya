package element.infix;

public class InfixSpecial extends InfixItem {
	
	public static final int _OPEN_PAREN = (int)'(';
	public static final int _CLOSE_PAREN = (int)')';
	public static final int _OPEN_CURLY = (int)'{';
	public static final int _CLOSE_CURLY = (int)'}';
	public static final int _COMMA = (int)',';
	public static final int _EQUALS = (int)'=';
	
	public static final InfixSpecial OPEN_PAREN = new InfixSpecial(_OPEN_PAREN);
	public static final InfixSpecial CLOSE_PAREN = new InfixSpecial(_CLOSE_PAREN);
	public static final InfixSpecial OPEN_CURLY = new InfixSpecial(_OPEN_CURLY);
	public static final InfixSpecial CLOSE_CURLY = new InfixSpecial(_CLOSE_CURLY);
	public static final InfixSpecial COMMA = new InfixSpecial(_COMMA);
	public static final InfixSpecial EQUALS = new InfixSpecial(_EQUALS);
	
	
	public InfixSpecial(int i) {
		super(InfixItem.SPECIAL);
		this.subtype = i;
		// TODO Auto-generated constructor stub
	}
	

	@Override
	public Object generateElementCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String typeString() {
		return "{s:" + (char)subtype + "}";
	}

	@Override
	public void organize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void desugar() {
		// TODO Auto-generated method stub
		
	}

}
