package aya.parser;

import aya.exceptions.parser.EndOfInputError;
import aya.exceptions.parser.SyntaxError;
import aya.util.UTF16;

public class StringParseUtils {

	public static String goToEnd(ParserString in, char termination) throws EndOfInputError {
		StringBuilder out = new StringBuilder();
		while (in.hasNext()) {
			int c = in.next();

			if (c == '\\' && in.hasNext()) {
				out.append(UTF16.surrogateToStr(c));
				out.append(in.nextStr());
			} else if (c == termination) {
				return out.toString();
			} else {
				out.append(UTF16.surrogateToStr(c));
			}
		}
		// End of input
		return out.toString();
	}
	
	public static String unescape(ParserString in) throws SyntaxError, EndOfInputError {
		StringBuilder str = new StringBuilder();
		while (in.hasNext()) {
			int c = in.next();
			if (c == '\\') {
				int escape = in.next();
				switch (escape) {
				case '$':
					str.append("$");
					break;
				case '}':
					str.append("}"); // For escaping documented comments
					break;
				case 'n':
					str.append('\n');
					break;
				case 't':
					str.append('\t');
					break;
				case 'r':
					str.append('\r');
					break;
				case 'b':
					str.append('\b');
					break;
				case 'f':
					str.append('\f');
					break;
				case '"':
					str.append('"');
					break;
				case '?':
					throw new SyntaxError("test", in.currentRef());
				case '\\':
					str.append('\\');
					break;
				case '{':
					StringBuilder sc = new StringBuilder(); // Special Char
					boolean specialComplete = false;

					while (in.hasNext()) {
						if (in.peek() == '}') {
							specialComplete = true;
							in.next(); // Skip the closing '}'
							break;
						}
						sc.append(in.nextStr());
					}

					if (!specialComplete) {
						// throw new SyntaxError("Early termination of special character in string
						// literal: " + str.toString());
						// Always return a valid result
						str.append("\\{").append(sc);
					} else {

						// Parse the character
						char specChar = CharacterParser.parse(sc.toString(), in.currentRef());
						if (specChar == CharacterParser.INVALID) {
							// throw new SyntaxError("'\\" + sc.toString() + "' is not a valid special
							// character");
							// Always return a valid result
							str.append("\\{").append(sc).append("}");
						}

						str.append(specChar);
					}
					break;

				default:
					// throw new SyntaxError("'" + escape + "' is not a valid escape character....
					// Always return a valid result
					str.append('\\').append(UTF16.surrogateToStr(escape));
				}
			} else {
				str.append(UTF16.surrogateToStr(c));
			}
		}
		return str.toString();
	}
	
}
