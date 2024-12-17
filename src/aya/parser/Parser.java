package aya.parser;

import java.util.ArrayList;

import aya.AyaStdIO;
import aya.StaticData;
import aya.exceptions.parser.EndOfInputError;
import aya.exceptions.parser.ParserException;
import aya.exceptions.parser.SyntaxError;
import aya.instruction.BlockLiteralInstruction;
import aya.instruction.DataInstruction;
import aya.instruction.Instruction;
import aya.instruction.InstructionStack;
import aya.instruction.ListLiteralInstruction;
import aya.instruction.index.GetExprIndexInstruction;
import aya.instruction.index.GetNumberIndexInstruction;
import aya.instruction.index.GetObjIndexInstruction;
import aya.instruction.index.GetVarIndexInstruction;
import aya.instruction.index.SetExprIndexInstruction;
import aya.instruction.index.SetNumberIndexInstruction;
import aya.instruction.index.SetObjIndexInstruction;
import aya.instruction.index.SetVarIndexInstruction;
import aya.instruction.op.ColonOps;
import aya.instruction.op.OperatorInstruction;
import aya.instruction.op.Ops;
import aya.instruction.variable.GetKeyVariableInstruction;
import aya.instruction.variable.GetVariableInstruction;
import aya.instruction.variable.QuoteGetKeyVariableInstruction;
import aya.instruction.variable.QuoteGetVariableInstruction;
import aya.instruction.variable.SetKeyVariableInstruction;
import aya.instruction.variable.SetVariableInstruction;
import aya.obj.Obj;
import aya.obj.block.BlockUtils;
import aya.obj.block.StaticBlock;
import aya.obj.list.List;
import aya.obj.number.Number;
import aya.obj.symbol.SymbolTable;
import aya.parser.token.TokenQueue;
import aya.parser.token.TokenStack;
import aya.parser.tokens.BlockToken;
import aya.parser.tokens.CDictToken;
import aya.parser.tokens.CharToken;
import aya.parser.tokens.DictToken;
import aya.parser.tokens.KeyVarToken;
import aya.parser.tokens.LambdaToken;
import aya.parser.tokens.ListToken;
import aya.parser.tokens.NamedOpToken;
import aya.parser.tokens.NumberToken;
import aya.parser.tokens.OperatorToken;
import aya.parser.tokens.SpecialToken;
import aya.parser.tokens.StdToken;
import aya.parser.tokens.StringToken;
import aya.parser.tokens.SymbolToken;
import aya.parser.tokens.Token;
import aya.parser.tokens.VarToken;
import aya.util.CharUtils;

/**
 * 0. Input String 1. tokenize: Converts characters and character sets to tokens
 * - Parses string and character literals - Identifies Operators - detects dot
 * operators - Identifies opening and closing delimiters 2. assemble: Assembles
 * the tokens into token groups based on context - Assembles list and blockEvaluator
 * literals - parses decimal numbers 3. generate: Generate Aya code based on the
 * tokens
 * 
 * @author Nick
 *
 */
public class Parser {
	
	public static char CDICT_CHAR = (char)162; // cent

	public static TokenQueue tokenize(ParserString in) throws ParserException {
		TokenQueue tokens = new TokenQueue();

		while (!in.isEmpty()) {
			char current = in.next();

			// Line Comment
			if (current == '.' && in.hasNext() && in.peek() == '#') {
				in.next(); // skip the '#'
				StringBuilder comment = new StringBuilder();

				while (in.hasNext() && in.peek() != '\n') {
					comment.append(in.next());
				}

				// Skip the last character
				if (in.hasNext()) {
					comment.append(in.next());
				}

				// Documentation comment?
				String comment_str = comment.toString();
				if (comment_str.length() > 0 && comment_str.charAt(0) == '?') {
					comment_str = comment_str.substring(1);
					StaticData.getInstance().addHelpText(comment_str);
				}

				continue;
			}

			// BlockEvaluator Comment
			if (current == '.' && in.hasNext() && in.peek() == '{') {
				in.next(); // Skip the '{'

				StringBuilder comment = new StringBuilder();
				boolean complete = false;
				while (in.hasNext(1)) {
					if (in.peek(0) == '.' && in.peek(1) == '}') {
						in.next();
						in.next(); // Skip the ".}"
						complete = true;
						break;
					}
					comment.append(in.next());
				}

				// Early input termination
				if (!complete) {
					while (in.hasNext()) {
						comment.append(in.next());
					}
				}

				// Add the documentation to Aya
				String comment_str = comment.toString();
				if (comment_str.length() > 0 && comment_str.charAt(0) == '?') {
					comment_str = comment_str.substring(1);
					StaticData.getInstance().addHelpText(comment_str);
				}
			}

			// Number Literals
			else if (Character.isDigit(current)) {
				in.backup();
				tokens.add(parseNumber(in));
			}
			
			// Negative Number Literal
			else if (current == '-' && in.hasNext() && Character.isDigit(in.peek())) {
				in.backup();
				tokens.add(parseNumber(in));
			}
			
			// Negative Decimal Literal (starting with -.)
			else if (current == '-' && in.hasNext()  && in.peek() == '.'
									&& in.hasNext(1) && Character.isDigit(in.peek(1))) {
				in.backup();
				tokens.add(parseNumber(in));
			}

			// Dot (operator/decimal)
			else if (current == '.') {
				if (in.hasNext()) {
					// Decimal
					if (Character.isDigit(in.peek())) {
						in.backup();
						tokens.add(parseNumber(in));
					}

					// Key Variable
					else if (SymbolTable.isBasicSymbolChar(in.peek())) {
						String varname = "" + in.next();
						while (in.hasNext() && SymbolTable.isBasicSymbolChar(in.peek())) {
							varname += in.next();
						}
						tokens.add(new KeyVarToken(varname, in.currentRef()));
					}

					else if (in.peek() == '"') {
						in.next(); // Skip opening "
						String varname = StringParseUtils.goToEnd(in, '"');
						tokens.add(new KeyVarToken(varname, in.currentRef()));
					}


					// Quote a function (.`)
					else if (in.peek() == '`') {
						tokens.add(new SpecialToken(Token.FN_QUOTE, ".`", in.currentRef()));
						in.next(); // Skip the '`'
					}

					// Dot Colon
					else if (in.peek() == ':') {
						tokens.add(new SpecialToken(Token.DOT_COLON, ".:", in.currentRef()));
						in.next(); // Skip the ':'
						
						// Quoted variable
						if (in.peek() == '"') {
							in.next(); // Skip open '
							String str = StringParseUtils.goToEnd(in, '"');
							tokens.add(new VarToken(str, in.currentRef()));
						}
					}

					// Plain Dot
					else if (in.peek() == '[') {
						tokens.add(new SpecialToken(Token.DOT, ".", in.currentRef()));
					}

					// Dot operator
					else if (in.peek() <= Ops.MAX_OP){
						tokens.add(new OperatorToken("" + in.next(), OperatorToken.DOT_OP, in.currentRef()));
					}
					
					else {
						tokens.add(new KeyVarToken(""+in.next(), in.currentRef()));
					}

				} else {
					throw new SyntaxError("Unexpected end of input after '.'", in.currentRef());
				}
			}

			// Math Operators
			else if (current == 'M') {
				try {
					tokens.add(new OperatorToken("" + in.next(), OperatorToken.MATH_OP, in.currentRef()));
				} catch (EndOfInputError e) {
					throw new SyntaxError("Expected op name after 'M'", in.currentRef());
				}
			}
			
			else if (current == Parser.CDICT_CHAR) { // cent
				tokens.add(new CDictToken(""+in.next(), in.currentRef()));
			}

			// Long String Literals
			else if (current == '"' && in.hasNext(1) && in.peek(0) == '"' && in.peek(1) == '"') {
				StringBuilder str = new StringBuilder();
				SourceStringRef startRef = in.currentRef();

				// Skip other quote chars
				in.next();
				in.next();

				while (true) {
					// String closed
					if (in.hasNext(2) && in.peek(0) == '"' && in.peek(1) == '"' && in.peek(2) == '"') {

						// false = do not interpolate
						SourceStringRef ref = in.currentRef();
						ref.inc(); // Go to end of string
						tokens.add(new StringToken(str.toString(), false, ref));

						// Skip closing quotes
						in.next();
						in.next();
						in.next();

						// Exit loop
						break;
					}
					// If there exists a character, add it
					else if (in.hasNext()) {
						str.append(in.next());

					}
					// Incomplete
					else {
						throw new SyntaxError("Unterminated long string literal (missing ending \"\"\")", startRef);
					}
				}
			}

			// String Literals
			else if (current == '"') {
				String str = StringParseUtils.goToEnd(in, '"');
				tokens.add(new StringToken(str, in.currentRef()));
			}

			// Character Literals
			else if (current == '\'') {
				if (!in.hasNext()) {
					throw new SyntaxError("Expected character name after open single quote", in.currentRef());
				}
				// Special Character
				if (in.peek() == '\\') {
					in.next(); // Skip the \ character
					StringBuilder sb = new StringBuilder();
					boolean complete = false;
					while (in.hasNext()) {
						if (in.peek() == '\'') {
							in.next(); // Skip the closing quote
							complete = true;
							break;
						}
						sb.append("" + in.next());
					}
					if (!complete) {
						throw new SyntaxError("Expected closing quote after character literal '\\" + sb.toString(), in.currentRef());
					}

					char specialChar;
					if (sb.length() == 0) {
						specialChar = '\\';
					} else {
						specialChar = CharacterParser.parse(sb.toString(), in.currentRef());
					}

					if (specialChar == CharacterParser.INVALID) {
						throw new SyntaxError("'\\" + sb.toString() + "' is not a valid special character", in.currentRef());
					}

					tokens.add(new CharToken("" + specialChar, in.currentRef()));

				}

				// Normal Character
				else {
					tokens.add(new CharToken("" + in.next(), in.currentRef()));
				}
			}

			// Variable Name
			else if (SymbolTable.isBasicSymbolChar(current)) {
				StringBuilder sb = new StringBuilder("" + current);
				while (in.hasNext() && SymbolTable.isBasicSymbolChar(in.peek())) {
					sb.append(in.next());
				}
				tokens.add(new VarToken(sb.toString(), in.currentRef()));
			}

			// Normal Operators
			else if (Ops.isOpChar(current)) {
				tokens.add(new OperatorToken("" + current, OperatorToken.STD_OP, in.currentRef()));
			}

			// Colon
			else if (current == ':') {
				
				if (in.hasNext()) {

					// Symbol
					if (in.peek() == ':') {
						in.next(); // Move to the next colon
						String sym = "";
						if (in.hasNext() && in.peek() == '"') {
							// Quoted symbol
							in.next(); // Skip '
							sym = StringParseUtils.goToEnd(in, '"');
						} else {

							// Fist, try to parse as simple variable
							while (in.hasNext() && SymbolTable.isBasicSymbolChar(in.peek())) {
								sym += in.next();
							}

							// If empty, check for operator or special character
							if (sym.equals("")) {
								if (in.hasNext()) {
									
									// Multi-char operator
									if (isMultiCharOpPrefix(in.peek())) {
										sym = ""+in.next();  // prefix
										sym += ""+in.next(); // op char
									} else {
										// Anything else: (single op, unicode symbol, etc.) single character
										sym = "" + in.next();
									}
									
								} else {
									throw new SyntaxError("Expected symbol name", in.currentRef());
								}
							}
						}

						tokens.add(new SymbolToken(sym, in.currentRef()));
					}
					
					// Named Operator
					else if (in.peek() == '(') {
						in.next(); // Skip '('
						StringBuilder sb = new StringBuilder();
						boolean done = false;
						while (in.hasNext()) {
							char c = in.next();
							if (c == ')') {
								done = true;
								break;
							} else {
								sb.append(c);
							}
						}
						if (done) {
							tokens.add(new NamedOpToken(sb.toString(), in.currentRef()));
						} else {
							throw new SyntaxError("Expected ')' after :(" + sb.toString(), in.currentRef());
						}
					}

					// Colon Pound
					else if (in.peek() == '#') {
						tokens.add(new SpecialToken(Token.COLON_POUND, ":#", in.currentRef()));
						in.next(); // Skip the #
					}
					
					// Dict Literal
					else if (in.peek() == '{') {
						tokens.add(new SpecialToken(Token.NEXT_BLOCK_IS_DICT, ":", in.currentRef()));
					}

					// Quoted variable
					else if (in.peek() == '"') {
						tokens.add(new SpecialToken(Token.COLON, ":", in.currentRef()));
						in.next(); // Skip open "
						String varname = StringParseUtils.goToEnd(in, '"');
						tokens.add(new VarToken(varname, in.currentRef()));
					}

					// Colon Operator
					else if (ColonOps.isColonOpChar(in.peek()) && in.peek() != '{' && in.peek() != '[') {
						tokens.add(new OperatorToken("" + in.next(), OperatorToken.COLON_OP, in.currentRef()));
					}

					// Special number
					else if (CharUtils.isDigit(in.peek()) || in.peek() == '-') {
						in.backup();
						tokens.add(parseNumber(in));
					}
					
					// Plain Colon
					else {
						tokens.add(new SpecialToken(Token.COLON, ":", in.currentRef()));
					}
				}

				// !hasNext()
				else {
					tokens.add(new SpecialToken(Token.COLON, ":", in.currentRef()));
				}

			} // end colon

			else {
				// Single Character Special Tokens
				SpecialToken tmp = SpecialToken.get(current, in.currentRef());
				if (tmp != null) {
					tokens.add(tmp);
				} else if (!Character.isWhitespace(current)){
					// Single character variable
					tokens.add(new VarToken(""+current, in.currentRef()));
				}
			}
		}

		return tokens;
	}

	public static NumberToken parseNumber(ParserString in) throws EndOfInputError, SyntaxError {
		if (!in.hasNext()) {
			throw new SyntaxError("Attempted to parse empty number string", in.currentRef());
		}
		char start = in.next();
		if (start == ':') {
			if (in.hasNext() && (CharUtils.isDigit(in.peek()) || in.peek() == '-')) {
				// Collect the special number
				StringBuilder specNum = new StringBuilder();
				while (in.hasNext()
						&& (CharUtils.isDigit(in.peek()) || CharUtils.isLowerAlpha(in.peek()) || in.peek() == '-' || in.peek() == '.')) {
					if (in.peek() == '.' && in.hasNext() && !CharUtils.isDigit(in.peek(1)))
						break;
					specNum.append(in.next());
				}
				return new NumberToken(specNum.toString(), true, in.currentRef());
			} else {
				throw new SyntaxError(in.toString() + " is not a valid number", in.currentRef());
			}
		} else if (CharUtils.isDigit(start) || start == '.' || start == '-') {
			StringBuilder num = new StringBuilder("" + start);

			while (in.hasNext() && Character.isDigit(in.peek())) {
				num.append(in.next());
			}

			if (start != '.') { // The start of the number was not a decimal, there may be one now
				// Decimal
				if (in.hasNext() && in.peek(0) == '.' && in.hasNext(1) && Character.isDigit(in.peek(1))) {
					num.append('.');
					in.next(); // Skip the '.'
					while (in.hasNext() && Character.isDigit(in.peek())) {
						num.append(in.next());
					}
				}
			}
			return new NumberToken(num.toString(), in.currentRef());
		} else {
			throw new SyntaxError(in.toString() + " is not a valid number", in.currentRef());
		}
	}

	public static TokenQueue assemble(TokenQueue in) throws EndOfInputError, SyntaxError {
		TokenQueue out = new TokenQueue();
		boolean next_block_is_dict = false;
		
		while (in.hasNext()) {
			Token current = in.next();
			
			
			switch (current.getType()) {
			case Token.NEXT_BLOCK_IS_DICT:
				next_block_is_dict = true;
				break;
			case Token.OPEN_CURLY:
				closeDelim(Token.OPEN_CURLY, Token.CLOSE_CURLY, 
					next_block_is_dict ? Token.DICT : Token.BLOCK, 
					in, out, current.getSourceStringRef());
				next_block_is_dict = false;
				break;
			case Token.OPEN_SQBRACKET:
				closeDelim(Token.OPEN_SQBRACKET, Token.CLOSE_SQBRACKET, Token.LIST, in, out, current.getSourceStringRef());
				break;
			case Token.OPEN_PAREN:
				closeDelim(Token.OPEN_PAREN, Token.CLOSE_PAREN, Token.LAMBDA, in, out, current.getSourceStringRef());
				break;

			// At this point, all delims should be balanced
			// If they aren't, throw an error
			case Token.CLOSE_CURLY:
				throw new SyntaxError("Unexpected token '}'", current.getSourceStringRef());
			case Token.CLOSE_PAREN:
				throw new SyntaxError("Unexpected token ')'", current.getSourceStringRef());
			case Token.CLOSE_SQBRACKET:
				throw new SyntaxError("Unexpected token ']'", current.getSourceStringRef());

			default:
				out.add(current);
			}
			
			if (next_block_is_dict && current.getType() != Token.NEXT_BLOCK_IS_DICT) {
				throw new SyntaxError("Internal error parsing this token. Expected next token to be a dict", current.getSourceStringRef());
			}
		}

		return out;
	}

	/**
	 * Assumes the first delim has been removed input = 1 2 3] output = out.add(new
	 * Token(Token.LIST, data))
	 * @throws EndOfInputError 
	 * @throws SyntaxError 
	 */
	public static void closeDelim(int open, int close, int type, TokenQueue in, TokenQueue out, SourceStringRef source) throws EndOfInputError, SyntaxError {
		TokenQueue innerTokens = new TokenQueue();
		StringBuilder debugStr = new StringBuilder();
		boolean complete = false;
		int brackets = 0;
		while (in.hasNext()) {
			int currentType = in.peek().getType();

			if (currentType == open) {
				brackets++;
			}

			else if (currentType == close) {
				if (brackets == 0) {
					in.next(); // skip the closing delim
					complete = true;
					break;
				} else {
					brackets--;
				}
			}
			debugStr.append(in.peek().getData()).append(" ");
			innerTokens.add(in.next());
		}

		
		if (!complete) {
			throw new SyntaxError(
					"No closing delimiter found", source);
		}

		innerTokens = assemble(innerTokens);

		switch (type) {
		case Token.DICT:
			out.add(new DictToken(debugStr.toString(), innerTokens.getArrayList(), source));
			break;
		case Token.BLOCK:
			out.add(new BlockToken(debugStr.toString(), innerTokens.getArrayList(), source));
			break;
		case Token.LIST:
			out.add(new ListToken(debugStr.toString(), innerTokens.getArrayList(), source));
			break;
		case Token.LAMBDA:
			out.add(new LambdaToken(debugStr.toString(), innerTokens.getArrayList(), source));
		}

	}

	public static InstructionStack generate(TokenQueue tokens_in) throws ParserException {
		InstructionStack is = new InstructionStack();
		TokenStack stk = new TokenStack(tokens_in);

		while (stk.hasNext()) {
			Token current = stk.pop();

			// COLON
			if (current.isa(Token.COLON)) {
				if (is.isEmpty()) {
					throw new SyntaxError("Expected token after ':'", current.getSourceStringRef());
				}
				Instruction next = is.pop();
				// Variable Assignment
				if (next instanceof GetVariableInstruction) {
					GetVariableInstruction v = ((GetVariableInstruction) next);
					is.push(new SetVariableInstruction(current.getSourceStringRef(), v.getSymbol()));
				} else {
					throw new SyntaxError("':' not followed by operator", current.getSourceStringRef());
				}
			}

			else if (current.isa(Token.DOT)) {
				if (is.isEmpty()) {
					throw new SyntaxError("Expected token after '.'", current.getSourceStringRef());
				}
				Instruction next = is.pop();

				if (next instanceof ListLiteralInstruction) {
					List l = ((ListLiteralInstruction) next).toListNoEval();
					if (l != null) {
						if (l.length() == 1) {
							Obj first = l.getExact(0);
							if (first.isa(Obj.NUMBER)) {
								is.push(new GetNumberIndexInstruction(current.getSourceStringRef(), ((Number)first).toInt()));
							} else {
								is.push(new GetObjIndexInstruction(current.getSourceStringRef(), first));
							}
						} else {
							throw new SyntaxError(
									"Invalid index: " + l.repr() + ": Index must contain exactly one element", current.getSourceStringRef());
						}
					} else {
						ListLiteralInstruction lli = (ListLiteralInstruction) next;
						ArrayList<Instruction> instructions = lli.getInstructions().getInstrucionList();
						if (instructions.size() == 1 && instructions.get(0) instanceof GetVariableInstruction) {
							// Small optimization for single variable indices
							is.push(new GetVarIndexInstruction(current.getSourceStringRef(), ((GetVariableInstruction) instructions.get(0)).getSymbol()));
						} else {
							is.push(new GetExprIndexInstruction(current.getSourceStringRef(), BlockUtils.fromIS(lli.getInstructions())));
						}
					}
				}
			}

			else if (current.isa(Token.DOT_COLON)) {
				if (is.isEmpty()) {
					throw new SyntaxError("Expected token after '.:'", current.getSourceStringRef());
				}
				Instruction next = is.pop();
				// Key Variable Assignment
				if (next instanceof GetVariableInstruction) {
					GetVariableInstruction v = ((GetVariableInstruction) next);
					is.push(new SetKeyVariableInstruction(current.getSourceStringRef(), v.getSymbol()));
				}

				// Index assignment
				else if (next instanceof ListLiteralInstruction) {
					List l = ((ListLiteralInstruction) next).toListNoEval();
					if (l != null) {
						if (l.length() == 1) {
							Obj first = l.getExact(0);
							if (first.isa(Obj.NUMBER)) {
								is.push(new SetNumberIndexInstruction(current.getSourceStringRef(), ((Number)first).toInt()));
							} else {
								is.push(new SetObjIndexInstruction(current.getSourceStringRef(), first));
							}
						} else {
							throw new SyntaxError(
									"Invalid index: " + l.repr() + ": Index must contain exactly one element", current.getSourceStringRef());
						}
					} else {
						ListLiteralInstruction lli = (ListLiteralInstruction) next;
						ArrayList<Instruction> instructions = lli.getInstructions().getInstrucionList();
						if (instructions.size() == 1 && instructions.get(0) instanceof GetVariableInstruction) {
							// Small optimization for single variable indices
							is.push(new SetVarIndexInstruction(current.getSourceStringRef(), ((GetVariableInstruction) instructions.get(0)).getSymbol()));
						} else {
							is.push(new SetExprIndexInstruction(current.getSourceStringRef(), BlockUtils.fromIS(lli.getInstructions())));
						}
					}
				}
			}

			// POUND
			else if (current.isa(Token.POUND)) {
				BlockLiteralInstruction blk_ins = captureUntilOp(is, tokens_in);
				is.push(new OperatorInstruction(current.getSourceStringRef(), Ops.OP_POUND));
				is.push(blk_ins);
			}

			else if (current.isa(Token.TICK)) {
				BlockLiteralInstruction blk_ins = captureUntilOp(is, tokens_in);
				is.push(blk_ins);
			}

			// COLON POUND
			else if (current.isa(Token.COLON_POUND)) {
				if (is.isEmpty()) {
					throw new SyntaxError("Expected token after infix operator ':#'", current.getSourceStringRef());
				}
				Instruction next = is.pop();
				// Apply a blockEvaluator to a list or dict
				is.push(new OperatorInstruction(current.getSourceStringRef(), ColonOps.OP_COLON_POUND));
				is.push(next);
			}

			else if (current.isa(Token.FN_QUOTE)) {
				if (stk.hasNext()) {
					if (stk.peek().isa(Token.VAR)) {
						VarToken t = (VarToken) stk.pop();
						is.push(new QuoteGetVariableInstruction(t.getSourceStringRef(), t.getSymbol()));
					} else if (stk.peek().isa(Token.KEY_VAR)) {
						KeyVarToken t = (KeyVarToken) stk.pop();
						is.push(new QuoteGetKeyVariableInstruction(t.getSourceStringRef(), t.getSymbol()));
					} else {
						throw new SyntaxError("Expected var or keyvar before quote (.`) token", current.getSourceStringRef());
					}
				} else {
					throw new SyntaxError("Expected var or keyvar before quote (.`) token", current.getSourceStringRef());
				}
			}

			else if (current.typeString().equals("special")) {
				throw new SyntaxError("Unexpected token", current.getSourceStringRef());
			}

			// Std Token
			else {
				is.push(((StdToken) current).getInstruction());
			}

		}

		return is;

	}
	
	private static BlockLiteralInstruction captureUntilOp(InstructionStack is, TokenQueue tokens_in) throws SyntaxError {
		SourceStringRef ref = tokens_in.peek().getSourceStringRef();
		if (is.isEmpty()) {
			throw new SyntaxError("Expected token when assembling blockEvaluator", ref);
		} else {
			Instruction next = is.pop();

			// Apply a blockEvaluator to a list
			if (next instanceof DataInstruction && ((DataInstruction) next).objIsa(Obj.BLOCK)) {
				throw new SyntaxError("Assertion Failed!!", tokens_in.peek().getSourceStringRef());
			} else if (next instanceof BlockLiteralInstruction) {
				return (BlockLiteralInstruction)next;
			} else {
				// Create a blockEvaluator and apply it to a list
				InstructionStack colonBlock = new InstructionStack();
				is.push(next); // Add next back in

				while (!is.isEmpty()) {
					Instruction o = is.pop();
					colonBlock.insert(0, o);
					if (o instanceof OperatorInstruction || o instanceof GetVariableInstruction
							|| o instanceof GetKeyVariableInstruction) {
						break;
					}
				}
				return new BlockLiteralInstruction(ref, BlockUtils.fromIS(colonBlock));
			}
		}
	}

	/**
	 * Compiles a string into a code blockEvaluator using input => tokenize => assemble =>
	 * generate
	 * @throws ParserException 
	 * @throws SyntaxError 
	 * @throws EndOfInputError 
	 */
	public static StaticBlock compile(SourceString source) throws EndOfInputError, SyntaxError, ParserException {
		InstructionStack is = compileIS(source);
		return BlockUtils.fromIS(is);
	}
	
	public static StaticBlock compileSafeOrNull(SourceString source, AyaStdIO io) {
		try {
			return compile(source);
		} catch (ParserException e) {
			io.err().println("Syntax Error: " + e.getSimpleMessage());
			return null;
		}
	}

	/**
	 * Compiles a string into instruction stack using input => tokenize => assemble
	 * => generate
	 * @throws ParserException 
	 * @throws SyntaxError 
	 * @throws EndOfInputError 
	 */
	public static InstructionStack compileIS(SourceString source) throws EndOfInputError, SyntaxError, ParserException {
		ParserString ps = new ParserString(source);
		return compileIS(ps);
	}

	public static InstructionStack compileIS(ParserString ps) throws EndOfInputError, SyntaxError, ParserException {
		return generate(assemble(tokenize(ps)));
	}

	private static boolean isMultiCharOpPrefix(char c) {
		return c == ':' || c == '.' || c == 'M';
	}

}
