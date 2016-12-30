package aya.parser;

import java.math.BigDecimal;
import java.math.BigInteger;

import aya.Aya;
import aya.entities.InstructionStack;
import aya.entities.Operation;
import aya.entities.operations.ColonOps;
import aya.entities.operations.Ops;
import aya.exceptions.EndOfInputError;
import aya.exceptions.SyntaxError;
import aya.obj.block.Block;
import aya.obj.dict.KeyVariable;
import aya.parser.token.TokenQueue;
import aya.parser.token.TokenStack;
import aya.parser.tokens.BlockToken;
import aya.parser.tokens.CharToken;
import aya.parser.tokens.KeyVarToken;
import aya.parser.tokens.LambdaToken;
import aya.parser.tokens.ListToken;
import aya.parser.tokens.NumberToken;
import aya.parser.tokens.OperatorToken;
import aya.parser.tokens.SpecialToken;
import aya.parser.tokens.StdToken;
import aya.parser.tokens.StringToken;
import aya.parser.tokens.TickToken;
import aya.parser.tokens.Token;
import aya.parser.tokens.VarToken;
import aya.variable.Variable;

/**
 * 0. Input String
 * 1. tokenize:	Converts characters and character sets to tokens 
 * 				- Parses string and character literals
 * 				- Identifies Operators
 * 					- detects dot operators
 * 				- Identifies opening and closing delimiters
 * 2. assemble: Assembles the tokens into token groups based on context
 * 				- Assembles list and block literals
 * 				- parses decimal numbers
 * 3. generate:	Generate Aya code based on the tokens
 * 
 * @author Nick
 *
 */
public class Parser {

	public static TokenQueue tokenize(Aya aya, String s) {
		TokenQueue tokens = new TokenQueue();
		ParserString in = new ParserString(s);
		
		
		while(!in.isEmpty()) {
			char current = in.next();
			
			//Line Comment
			if (current == '.' && in.hasNext() && in.peek() == '#') {
				in.next(); //skip the '#'
				
				//Help Text
				if(in.hasNext() && in.peek() == '?') {
					in.next(); //Skip the '?'
					StringBuilder sb = new StringBuilder();
					while(in.hasNext() && in.peek() != '\n') {
						sb.append(in.next());
					}
					//Append the last character
					if(in.hasNext()) {
						sb.append(in.next());
					}
					String doc = formatString(sb.toString()).trim();
					aya.addHelpText(doc);
				}
				
				else {
					while(in.hasNext() && in.peek() != '\n') {
						in.next();
					}
					//Skip the last character
					if(in.hasNext()) {
						in.next();
					}
				}
				continue;
			}
			
			//Block Comment
			if(current == '.' && in.hasNext() && in.peek() == '{') {
				in.next(); //Skip the '{'
				
				//Determine if the block is documentation
				boolean isDocCode = false;
				StringBuilder docs = null;
				if(in.hasNext() && in.peek() == '?') {
					in.next(); //Skip the '?'
					isDocCode = true;
					docs = new StringBuilder();

				}
				
				//Skip or collect the block
				boolean complete = false;
				while(in.hasNext(1)) {
					if (in.peek(0) == '.' && in.peek(1) == '}') {
						in.next();
						in.next(); //Skip the ".}"
						complete = true;
						break;
					}
					
					if (isDocCode) {
						docs.append(in.next());
					} else {
						in.next();
					}
				}
					
				//Early input termination
				if (!complete) {
					while(in.hasNext()) {in.next();}
				}
				
				//Add the documentation to Aya
				if(isDocCode) {
					String doc = formatString(docs.toString()).trim();
					aya.addHelpText(doc);
				}
			}
			
			//Number Literals
			else if(Character.isDigit(current)) {
				StringBuilder num = new StringBuilder(""+current);
				
				while(in.hasNext() && Character.isDigit(in.peek())) {
					num.append(in.next());
				}
				
				//Decimal
				if(in.hasNext() && in.peek(0) == '.' && in.hasNext(1) && Character.isDigit(in.peek(1)) ) {
					num.append('.');
					in.next(); //Skip the '.'
					while(in.hasNext() && Character.isDigit(in.peek())) {
						num.append(in.next());
					}
				}
				
				
				tokens.add(new NumberToken(num.toString()));
			}
			
			//Dot (operator/decimal)
			else if (current == '.') {
				if(in.hasNext()) {
					//Decimal
					if (Character.isDigit(in.peek())) {
						StringBuilder num = new StringBuilder("0.");
						while(in.hasNext() && Character.isDigit(in.peek())) {
							num.append(in.next());
						}
						tokens.add(new NumberToken(num.toString()));
					}
					
					//Key Variable
					else if ('a' <= in.peek() && in.peek() <= 'z') {
						String varname = ""+in.next();
						while(in.hasNext() && 'a' <= in.peek() && in.peek() <= 'z') {
							varname += in.next();
						}
						tokens.add(new KeyVarToken(varname));
					}
					
					//Special Character Key Variable
					else if (CharacterParser.isSpecialChar(in.peek())) {
						tokens.add(new KeyVarToken(CharacterParser.getName(in.next())));
					}
					
					// Dot Colon
					else if (in.peek() == ':') {
						tokens.add(SpecialToken.DOT_COLON);
					}
					
					//Dot operator
					else {
						tokens.add(new OperatorToken(""+in.next(), OperatorToken.DOT_OP));
					}
				} else {
					throw new SyntaxError("Unexpected end of input after '.'" + in.toString());
				}
			}
			
			//Math Operators
			else if (current == 'M') {
				try {
					tokens.add(new OperatorToken(""+in.next(), OperatorToken.MATH_OP));
				} catch (EndOfInputError e) {
					throw new SyntaxError("Expected op name after 'M'" + in.toString());
				}
			}
			
			//Long String Literals
			else if (current == '"' && in.hasNext(1) && in.peek(0) == '"' && in.peek(1) == '"') {
				StringBuilder str = new StringBuilder();
				
				//Skip other quote chars
				in.next(); 
				in.next();
				
				while(true) {
					//String closed
					if(in.hasNext(2) && in.peek(0) == '"' && in.peek(1) == '"' && in.peek(2) == '"') {
						
						//false = do not interpolate
						tokens.add(new StringToken(str.toString(),false));
						
						//Skip closing quotes
						in.next();
						in.next();
						in.next();
						
						//Exit loop
						break;
					} 
					//If there exists a character, add it
					else if (in.hasNext()) {
						str.append(in.next());

					}
					//Incomplete
					else {
						throw new SyntaxError("Incomplete long string literal: " + str.toString());
					}
				}
			}
			
			//String Literals
			else if (current == '"') {
				StringBuilder str = new StringBuilder();
				boolean complete = false;
				while(in.hasNext()) {
					char c = in.next();
					if (c == '\\') {
						char escape = in.next();
						switch(escape) {
						case '}':
							str.append("}"); //For escaping documented comments
							break;
						case 'n':
							str.append('\n');
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
						case 't':
							str.append('\t');
							break;
						case '"':
							str.append('"');
							break;
						case '\\':
							str.append('\\');
							break;
						case '{':
							StringBuilder sc = new StringBuilder(); //Special Char
							boolean specialComplete = false;
							
							while (in.hasNext()) {
								if(in.peek() == '}') {
									specialComplete = true;
									in.next(); //Skip the closing '}'
									break;
								}
								sc.append(in.next());
							}
							
							if(!specialComplete) {
								throw new SyntaxError("Early termination of special character in string literal: " + str.toString());
							}
							
							//Parse the character
							char specChar = CharacterParser.parse(sc.toString());
							if(specChar == CharacterParser.INVALID) {
								throw new SyntaxError("'\\" + sc.toString() + "' is not a valid special character");
							}
							
							str.append(specChar);
							break;
							
						default:
							//throw new SyntaxError("'" + escape + "' is not a valid escape character in the string \""
									//+ str.toString() + "\\" + escape + " ...\"" + in.toString());	
							str.append("\\").append(escape);
						}
					}
					else if(c == '"') {
						complete = true;
						break;
					} else {
						str.append(c);
					}
				}
				if(!complete) {
					throw new SyntaxError("Expected closing quote after string \"" + str.toString());
				}
				tokens.add(new StringToken(str.toString()));
			}
			
			//Character Literals
			else if (current == '\'') {
				if(!in.hasNext()) {
					throw new SyntaxError("Expected character name after '''" + in.toString());
				}
				//Special Character
				if(in.peek() == '\\') {
					in.next(); //Skip the \ character
					StringBuilder sb = new StringBuilder();
					boolean complete = false;
					while(in.hasNext()) {
						if (in.peek() == '\'') {
							in.next(); //Skip the closing quote
							complete = true;
							break;
						}
						sb.append(""+in.next());
					}
					if(!complete) {
						throw new SyntaxError("Expected closing quote after character literal '\\" + sb.toString());
					}
					
					char specialChar;
					if (sb.length() == 0) {
						specialChar = '\\';
					} else {
						specialChar = CharacterParser.parse(sb.toString());
					}
					
					if(specialChar == CharacterParser.INVALID) {
						throw new SyntaxError("'\\" + sb.toString() + "' is not a valid special character");
					}
					
					tokens.add(new CharToken(""+specialChar));
					
				} 
				
				//Based Number
				//Convert the number to base 10 NUM token so that the rest of the 
				//parser sees it as a normal base 10 integer
				else if (in.peek() == '#') {
					in.next(); //Skip the #
					
					//Determine the base
					char base;
					if(in.hasNext()) {
						base = in.next();
						if(base == 'b') {
							base = (char)2;
						} else if (base == 'h') {
							base = (char)16;
						} else {
							throw new SyntaxError("Base " + (int)base + "is not a valid base id");
						}
					} else {
						throw new SyntaxError("Based number identifier '# must be followed by a number and a closing single quote");
					}
					
					//Capture the number
					StringBuilder sb = new StringBuilder();
					boolean complete = false;
					while(in.hasNext()) {
						if (in.peek() == '\'') {
							in.next(); //Skip the closing quote
							complete = true;
							break;
						}
						sb.append(""+in.next());
					}
					if(!complete) {
						throw new SyntaxError("Expected closing quote after based number literal '#" + sb.toString());
					}
					String num = sb.toString().trim();
					
					BigDecimal n = null;
					try {
						//The token will be properly converted to an int 
						//or a BigDecimal in the generate function
						n = new BigDecimal(new BigInteger(num, base));
					} catch (NumberFormatException e) {
						throw new SyntaxError("Cannot parse " + num + " using base " + (int)base);
					}

					tokens.add(new NumberToken(""+n));
				}
				
				//Normal Character
				else {
					tokens.add(new CharToken(""+in.next()));
				}
			}
			
			//Variable Name
			else if (isLowerAlpha(current)) {
				StringBuilder sb = new StringBuilder(""+current);
				while (in.hasNext() && isLowerAlpha(in.peek())) {
					sb.append(in.next());
				}
				if(sb.length() > 12) {
					Aya.instance.getOut().printWarn("Only the first 12 characters of a variable name are used. Ignoring '..."
							+ sb.toString().substring(12, sb.length()) + "' in " + sb.toString());
				}
				tokens.add(new VarToken(sb.toString()));
			}
			
			//Normal Operators
			else if (Ops.isOpChar(current)) {
				tokens.add(new OperatorToken(""+current, OperatorToken.STD_OP ));
			}
			
			
			//Colon
			else if (current == ':') {
				//Colon Operator 
				if (in.hasNext() && ColonOps.isColonOpChar(in.peek())) {
					tokens.add(new OperatorToken(""+in.next(), OperatorToken.COLON_OP));
				} 
				// Normal Colon
				else {
					tokens.add(SpecialToken.COLON);
				}
			}
			
			//Special Character Variables
			else if (CharacterParser.isSpecialChar(current)) {
				tokens.add(new VarToken(CharacterParser.getName(current)));
			}
			
			//Single Character Special Tokens
			else {
				SpecialToken tmp = SpecialToken.get(current);
				if (tmp != null) {
					tokens.add(tmp);
				}
				//else, ignore it
			}
		}
		
		return tokens;
	}
	
	public static TokenQueue assemble(TokenQueue in) {
		TokenQueue out = new TokenQueue();
		
		while (in.hasNext()) {
			Token current = in.next();
			
			switch(current.getType()) {
			case Token.OPEN_CURLY:
				closeDelim(Token.OPEN_CURLY, Token.CLOSE_CURLY, Token.BLOCK,in,out);
				break;
			case Token.OPEN_SQBRACKET:
				closeDelim(Token.OPEN_SQBRACKET, Token.CLOSE_SQBRACKET, Token.LIST,in,out);
				break;
			case Token.OPEN_PAREN:
				closeDelim(Token.OPEN_PAREN, Token.CLOSE_PAREN, Token.LAMBDA,in,out);
				break;
				
			//At this point, all delims should be balanced
			//If they aren't, throw an error
			case Token.CLOSE_CURLY:
				throw new SyntaxError("Unexpected token '}'");
			case Token.CLOSE_PAREN:
				throw new SyntaxError("Unexpected token ')'");
			case Token.CLOSE_SQBRACKET:
				throw new SyntaxError("Unexpected token ']'");
				
			case Token.DOT:
				throw new RuntimeException("Unexpected DOT operator. parser needs to be fixed");
				
			case Token.TICK:
				int ticks = 1;
				while(in.hasNext() && in.peek().isa(Token.TICK)) {
					ticks++;
					in.next();
				}
				out.add(new TickToken(ticks));
			break;
				
			default:
				out.add(current);
			}
		}
		
		return out;
	}
	
	/** Assumes the first delim has been removed 
	 * input = 1 2 3]
	 * output = out.add(new Token(Token.LIST, data))*/
	public static void closeDelim(int open, int close, int type, TokenQueue in, TokenQueue out) {
		TokenQueue innerTokens = new TokenQueue();
		StringBuilder debugStr = new StringBuilder();
		boolean complete = false;
		int brackets = 0;
		while(in.hasNext()) {
			int currentType = in.peek().getType();
			
			if(currentType == open) {
				brackets++;
			}
			
			else if (currentType == close) {
				if (brackets == 0) {
					in.next(); //skip the closing delim
					complete = true;
					break;
				} else {
					brackets--;
				}
			}
			debugStr.append(in.peek().getData()).append(" ");
			innerTokens.add(in.next());
		}
		
		if(!complete) {
			throw new SyntaxError("Expected closing "+ SpecialToken.quickString(type) +" delimiter after " + debugStr.toString());
		}
		
		innerTokens = assemble(innerTokens);
		
		switch(type) {
		case Token.BLOCK:
			out.add(new BlockToken(debugStr.toString(), innerTokens.getArrayList()));
			break;
		case Token.LIST:
			out.add(new ListToken(debugStr.toString(), innerTokens.getArrayList()));
			break;
		case Token.LAMBDA:
			out.add(new LambdaToken(debugStr.toString(), innerTokens.getArrayList()));
		}
		
	}
	
	public static InstructionStack generate(TokenQueue tokens_in) {
		InstructionStack is = new InstructionStack();
		TokenStack stk = new TokenStack(tokens_in);
		
		while (stk.hasNext()) {
			Token current = stk.pop();
			
			//COLON
			if (current.isa(Token.COLON)) {
				if(is.isEmpty()) {
					throw new SyntaxError("Expected token after ':' in:\n\t" + tokens_in.toString());
				}
				Object next = is.pop();
				//Variable Assignment
				if (next instanceof Variable) {
					Variable v = ((Variable)next);
					v.flagBind();
					is.push(v);
				}
				
				//Apply block to list
				else if (next instanceof Block) {
					is.push(Ops.APPLY_TO);
					is.push(next);
				}
				//Create a block and apply it to a list
				else {
					Block colonBlock = new Block();
					is.push(next); //Add next back in
					
					while (!is.isEmpty()) {
						Object o = is.pop();
						colonBlock.getInstructions().insert(0, o);
						if(o instanceof Operation || o instanceof Variable) {
							break;
						}
					}
					is.push(Ops.APPLY_TO);
					is.push(colonBlock);	
				}
			}
			
			else if (current.isa(Token.DOT_COLON)) {
				if(is.isEmpty()) {
					throw new SyntaxError("Expected token after '.:' in:\n\t" + tokens_in.toString());
				}
				Object next = is.pop();
				//Key Variable Assignment
				if (next instanceof Variable) {
					KeyVariable kv = new KeyVariable(((Variable)next).getID());
					kv.flagBind();
					is.push(kv);
				}
			}
			
			//POUND
			else if (current.isa(Token.POUND)) {
				if(is.isEmpty()) {
					throw new SyntaxError("Expected token after '#' in:\n\t" + tokens_in.toString());
				}
				Object next = is.pop();	
				//Apply a block to a list
				if (next instanceof Block) {
					is.push(Ops.getOp('#'));
					is.push(next);
				}
				//Create a block and apply it to a list
				else {
					Block colonBlock = new Block();
					is.push(next); //Add next back in
					
					while (!is.isEmpty()) {
						Object o = is.pop();
						colonBlock.getInstructions().insert(0, o);
						if(o instanceof Operation || o instanceof Variable) {
							break;
						}
					}
					is.push(Ops.getOp('#'));
					is.push(colonBlock);	
				}
			}
			
			else if (current.typeString().equals("special")) {
				throw new SyntaxError("Unexpected token in:\n\t" + tokens_in.toString());
			}
			
			//Std Token
			else {
				is.push(((StdToken)current).getAyaObj());
			}
			
		}
		
		return is;

	}
	
	private static String formatString(String input) {
		ParserString in = new ParserString(input);
		StringBuilder str = new StringBuilder();
		while(in.hasNext()) {
			char c = in.next();
			if (c == '\\') {
				char escape = in.next();
				switch(escape) {
				case '$':
					str.append("\\$");
					break;
				case '}':
					str.append("}"); //For escaping documented comments
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
				case '\\':
					str.append('\\');
					break;
				case '{':
					StringBuilder sc = new StringBuilder(); //Special Char
					boolean specialComplete = false;
					
					while (in.hasNext()) {
						if(in.peek() == '}') {
							specialComplete = true;
							in.next(); //Skip the closing '}'
							break;
						}
						sc.append(in.next());
					}
					
					if(!specialComplete) {
						//throw new SyntaxError("Early termination of special character in string literal: " + str.toString());
						//Always return a valid result
						str.append("\\{").append(sc);
					} else {
					
						//Parse the character
						char specChar = CharacterParser.parse(sc.toString());
						if(specChar == CharacterParser.INVALID) {
							//throw new SyntaxError("'\\" + sc.toString() + "' is not a valid special character");
							//Always return a valid result
							str.append("\\{").append(sc).append("}");
						}
						
						str.append(specChar);
					}
					break;
					
				default:
					//throw new SyntaxError("'" + escape + "' is not a valid escape character....
					//Always return a valid result
					str.append('\\').append(escape);
				}
			} else if (c == '$') {
			
			} else {
				str.append(c);
			}
		}
		return str.toString();
	}
	
	
	
	
	/** Returns true if the character is lowercase a-z */
	private static boolean isLowerAlpha(char c) {
		return (c >= 'a' && c <= 'z');
	}
	

	
	/** Compiles a string into a code block using input => tokenize => assemble => generate */
	public static Block compile(String s, Aya aya) {
		return new Block(generate(assemble(tokenize(aya, s))));
	}
	
	/** Compiles a string into instruction stack using input => tokenize => assemble => generate */
	public static InstructionStack compileIS(String s, Aya aya) {
		return generate(assemble(tokenize(aya, s)));
	}

}
