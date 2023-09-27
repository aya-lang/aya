package aya.parser;

import aya.exceptions.parser.SyntaxError;
import aya.util.NamedCharacters;
import aya.util.StringUtils;

public class CharacterParser {
	public static final String TAB_STR = "  ";
	public static final char INVALID = Character.MAX_VALUE;
	

	
	/**  
	 * Returns Character.MAX_VALUE if char is invalid 
	 * @throws SyntaxError */
	public static char parse(String in, SourceStringRef source) throws SyntaxError {
		String s = in;

		//Invalid Character
		if (s.length() < 1) {
			return INVALID;
		}
		
		//Single Character
		else if (s.length() == 1) {
			char c = s.charAt(0);
			switch (c) {
			case 'n':
				return '\n';
			case 't':
				return '\t';
			case 'r':
				return '\r';
			case 'b':
				return '\b';
			case 'f':
				return '\f';
			case '0':
				return '\0';
				
			default:
				return c;
			}
		}
		
		//Hex Character
		else if (s.charAt(0) == 'x') {
			s = s.substring(1, s.length()).trim();
			if (StringUtils.isHex(s)) {
				try {
					return (char)Integer.parseInt(s, 16);
				} catch (NumberFormatException e) {
					throw new SyntaxError("Cannot parse character as hex value in " + in, source);
				}
			} else {
				return INVALID;
			}
		} 
	
		// Decimal Character
		else if (s.charAt(0) == '0') {
			//s = s.substring(1, s.length()).trim()
			try {
				return (char)Integer.parseInt(s);
			} catch (NumberFormatException e) {
				throw new SyntaxError("Cannot parse decimal character value in " + in, source);
			}
		} 
		
		//Special character from list
		else {
			Character c = NamedCharacters.get(s);
			if(c == null) {
				return INVALID;
			}
			return c;
		}
	}
		
	
	public static String convertCharTabPress(String s) {
		char[] chars = s.toCharArray();
		
		if(s.length() == 0) {
			return TAB_STR;
		}
		
		StringBuilder charName = new StringBuilder();
		StringBuilder otherText = new StringBuilder();
		int i = chars.length-1;
		while (chars[i] != '\\') {
			charName.append(chars[i--]);
			if(i < 0) {
				return s+TAB_STR; //No special character
			}
		}
		i--; //Skip the '\'
		while (i >= 0) {
			otherText.append(chars[i--]);
		}
		
		charName.reverse();
		otherText.reverse();
		
		char c = INVALID;
		try {
			SourceString ss = new SourceString(s, "<CharacterParser.convertCharTabPress>");
			c = parse(charName.toString(), ss.ref(0));
		} catch (SyntaxError e) {
			c = INVALID; //Don't tab complete
		}
		
		if (c == INVALID || charName.length() == 1) {
			return s+TAB_STR; //No valid character, append the normal tab
		} else {
			return otherText.toString() + c;
		}
	}
}
