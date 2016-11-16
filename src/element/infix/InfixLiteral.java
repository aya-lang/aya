package element.infix;


import element.parser.tokens.Token;

public class InfixLiteral extends InfixItem {
	/** Type constants */
	public static final int NUM = Token.NUMERIC;
	public static final int STRING = Token.STRING;
	public static final int BLOCK = Token.BLOCK;
	
	/** The literal as it appears in the source */
	private String name;

	/** Literal constructor
	 * 
	 * @param name the literal as it appears in the source
	 * @param literalType the type of the literal (string, int, etc.)
	 */
	public InfixLiteral(String name, int literalType) {
		super(InfixItem.LITERAL);
		this.subtype = literalType;
		this.name = name;
	}
	
	/** Returns the literal as a string as it appears in the source */
	public String getName() {
		return name;
	}
	
	/** Generates the element literal */
	public Object generateElementCode() {
		switch(subtype) {
		case NUM:
			return Integer.parseInt(name);
		case STRING:
			return name;
		default:
			throw new RuntimeException("Cannot generate element code for literal type " + this.subtype);
		}
	}
	
	/** Returns a string representation of the type l[ (type) ] */
	public String typeString() {
		String s = "{l[";
		switch (this.subtype) {
		case NUM:
			s += "I";
			break;
		case STRING:
			s+= "S";
			break;
		default:
			s += "?";
		}
		return s + "]" + name + "}";
	}

	@Override
	public void organize() {
		//Do Nothing
		
	}

	@Override
	public void desugar() {
		//Do Nothing
	}

	
}