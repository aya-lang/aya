package aya.parser.tokens;

import aya.instruction.Instruction;
import aya.parser.SourceStringRef;
import aya.util.UTF16;

public class SpecialToken extends Token {
	String name;
		
	public SpecialToken(int type, String name, SourceStringRef source) {
		super(type, source);
		this.name = name;
		this.data = name;
	}

	@Override
	public String typeString() {
		return "special";
	}
	
	public String getName() {
		return name;
	}

	@Override
	public Instruction getInstruction() {
		throw new RuntimeException("Cannot generate aya code for special token");
	}

	/** Converts a character into a token representation */
	public static SpecialToken get(int c, SourceStringRef source) {
		return UTF16.is2Byte(c) ? get((char) c, source) : null;
	}

	/** Converts a character into a token representation */
	public static SpecialToken get(char c, SourceStringRef source) {
		int type = 0;
		switch(c) {
		case ',':
			type = COMMA;
			break;
		case '.':
			type = DOT;
			break;
		case '`':
			type = TICK;
			break;
		case ':':
			type = COLON;
			break;
		case '(':
			type = OPEN_PAREN;
			break;
		case ')':
			type = CLOSE_PAREN;
			break;
		case '[':
			type = OPEN_SQBRACKET;
			break;
		case ']':
			type = CLOSE_SQBRACKET;
			break;
		case '{':
			type = OPEN_CURLY;
			break;
		case '}':
			type = CLOSE_CURLY;
			break;
		case '#':
			type = POUND;
			break;
		default:
			return null;
		}
		return new SpecialToken(type, ""+c, source);
	}
}
