package element.parser.tokens;

public class SpecialToken extends Token {
	String name;
		
	public static final SpecialToken COMMA 				= new SpecialToken(Token.COMMA,",");
	public static final SpecialToken DOT 				    = new SpecialToken(Token.DOT, ".");
	public static final SpecialToken OPEN_PAREN			= new SpecialToken(Token.OPEN_PAREN, "(");
	public static final SpecialToken CLOSE_PAREN			= new SpecialToken(Token.CLOSE_PAREN, ")");
	public static final SpecialToken OPEN_SQBRACKET		= new SpecialToken(Token.OPEN_SQBRACKET, "[");
	public static final SpecialToken CLOSE_SQBRACKET		= new SpecialToken(Token.CLOSE_SQBRACKET, "]");
	public static final SpecialToken OPEN_CURLY			= new SpecialToken(Token.OPEN_CURLY, "{");
	public static final SpecialToken CLOSE_CURLY			= new SpecialToken(Token.CLOSE_CURLY, "}");
	public static final SpecialToken TICK				    = new SpecialToken(Token.TICK, "`");
	public static final SpecialToken COLON			    = new SpecialToken(Token.COLON, ":");
	public static final SpecialToken POUND				= new SpecialToken(Token.POUND, "#");
	public static final SpecialToken TRUE				    = new SpecialToken(Token.BOOL_TRUE, "T");
	public static final SpecialToken FALSE				= new SpecialToken(Token.BOOL_FALSE, "F");	
	
	
	public SpecialToken(int type, String name) {
		super(type);
		this.name = name;
		this.data = name;
	}
	
	/** Converts a character into a token representation */
	public static SpecialToken get(char c) {
		switch(c) {
		case ',':
			return COMMA;
		case '.':
			return DOT;
		case '`':
			return TICK;
		case ':':
			return COLON;
		case '(':
			return OPEN_PAREN;
		case ')':
			return CLOSE_PAREN;
		case '[':
			return OPEN_SQBRACKET;
		case ']':
			return CLOSE_SQBRACKET;
		case '{':
			return OPEN_CURLY;
		case '}':
			return CLOSE_CURLY;
		case '#':
			return POUND;
		default:
			return null;
		}
	}
	
	/** Returns a string representation of a type (static method) */
	public static String quickString(int t) {
		switch(t) {
		case STRING:
			return "string";
		case BLOCK:
			return "block";
		case VAR:
			return "var";
		case EXTENDED:
			return "extended";
		case OP:
			return "op";
		case NUM:
			return "num";
		case CHAR:
			return "character";
		case BIGNUM:
			return "bignum";
		case Token.COMMA:
			return "comma";
		case LAMBDA:
			return "lambda";
		case Token.TICK:
			return "tick(s)";
		case Token.DOT:
			return "dot";
		case Token.COLON:
			return "colon";
		case NUMERIC:
			return "num";
		case Token.CLOSE_CURLY:
			return "Ccurly";
		case Token.OPEN_CURLY:
			return "Ocurly";
		case Token.CLOSE_PAREN:
			return "Cparen";
		case Token.OPEN_PAREN:
			return "Oparen";
		case Token.OPEN_SQBRACKET:
			return "Osqbrack";
		case Token.CLOSE_SQBRACKET:
			return "Csqbrack";
		case OP_MATH:
			return "math op";
		case OP_DOT:
			return "dot op";
		case LIST:
			return "list";
				
		default:
			return "not specified";
				
		}
	}


	@Override
	public String typeString() {
		return "special";
	}
	
	public String getName() {
		return name;
	}

	@Override
	public Object getElementObj() {
		throw new RuntimeException("Cannot generate element code for special token");
	}
}
