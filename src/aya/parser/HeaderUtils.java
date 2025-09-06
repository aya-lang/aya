package aya.parser;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import aya.AyaPrefs;
import aya.exceptions.parser.EndOfInputError;
import aya.exceptions.parser.ParserException;
import aya.exceptions.parser.SyntaxError;
import aya.instruction.Instruction;
import aya.instruction.InstructionStack;
import aya.instruction.variable.QuoteGetVariableInstruction;
import aya.instruction.variable.assignment.Assignment;
import aya.instruction.variable.assignment.CopyAssignment;
import aya.instruction.variable.assignment.SimpleAssignment;
import aya.instruction.variable.assignment.TypedAssignment;
import aya.instruction.variable.assignment.UnpackAssignment;
import aya.obj.block.BlockUtils;
import aya.obj.block.CheckReturnTypeGenerator;
import aya.obj.block.StaticBlock;
import aya.obj.dict.Dict;
import aya.obj.number.Num;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;
import aya.parser.token.TokenQueue;
import aya.parser.tokens.LambdaToken;
import aya.parser.tokens.ListToken;
import aya.parser.tokens.OperatorToken;
import aya.parser.tokens.SymbolToken;
import aya.parser.tokens.Token;
import aya.parser.tokens.VarToken;
import aya.util.Pair;
import aya.util.Triple;

public class HeaderUtils {
	
	public static class HeaderInfo {
		public ArrayList<Assignment> args;
		public Dict locals;
		public HashMap<Symbol, StaticBlock> captures;
		public CheckReturnTypeGenerator ret_types;
		
		public HeaderInfo(ArrayList<Assignment> args, Dict locals, HashMap<Symbol, StaticBlock> captures,
				CheckReturnTypeGenerator ret_types) {
			this.args = args;
			this.locals = locals;
			this.captures = captures;
			this.ret_types = ret_types;
			
		}
	}

	// args, locals, captures
	public static HeaderInfo generateBlockHeader(TokenQueue tokens) throws ParserException {
		var split_tokens = splitAtColon(tokens);
		var arg_and_ret_type_tokens = splitAtArrow(split_tokens.first());

		boolean has_arrow = arg_and_ret_type_tokens.first();
		TokenQueue arg_tokens = arg_and_ret_type_tokens.second();
		TokenQueue ret_type_tokens = arg_and_ret_type_tokens.third();
		TokenQueue locals_and_captures_tokens = split_tokens.second();
	
		// Args
		ArrayList<Assignment> args = generateBlockHeaderArgs(arg_tokens);
		// Get all variable names used by args
		HashSet<Symbol> names = checkDuplicateArgs(args);
		
		// Return Types
		// ret_types may be null
		CheckReturnTypeGenerator ret_types = generateReturnTypes(ret_type_tokens, has_arrow);

		// Locals & Captures
		Pair<Dict, HashMap<Symbol, StaticBlock>> locals_and_captures = generateBlockHeaderDefaults(locals_and_captures_tokens, names);
		Dict locals = locals_and_captures.first();
		HashMap<Symbol, StaticBlock> captures = locals_and_captures.second();
				
		// Null checks
		if (args.size() == 0) args = null;
		if (locals.size() == 0) locals = null;
		if (captures.size() == 0) captures = null;
		
		return new HeaderInfo(args, locals, captures, ret_types);
	}
	


	/** Check to make sure there are no duplicate names in the locals and arguments
	 * 
	 * @param tokens
	 * @return
	 * @throws SyntaxError 
	 * @throws ParserException
	 */
	private static HashSet<Symbol> checkDuplicateArgs(ArrayList<Assignment> args) throws SyntaxError {
		HashSet<Symbol> names = new HashSet<Symbol>();
		for (Assignment a : args) {
			for (Symbol s : a.getNames()) {
				checkDuplicate(names, s, a.getSource());
			}
		}
		return names;
	}
	
	/** Helper function for checkDuplicateArgs
	 * 
	 * @param names
	 * @param a
	 * @throws SyntaxError 
	 */
	private static void checkDuplicate(HashSet<Symbol> names, Symbol name, SourceStringRef ref) throws SyntaxError {
		if (names.contains(name)) {
			throw new SyntaxError("Duplicate variable name", ref);
		} else {
			names.add(name);
		}	
	}

	private static ArrayList<Assignment> generateBlockHeaderArgs(TokenQueue tokens) throws ParserException {
		ArrayList<Assignment> out = new ArrayList<Assignment>();
		while (tokens.hasNext()) {
			Assignment arg = nextArg(tokens);
			out.add(arg);
		}
		return out;
	}
	
	private static Assignment nextArg(TokenQueue tokens) throws EndOfInputError, SyntaxError, ParserException {
		Token current = tokens.peek();
		
		// If we encounter a symbol or double colon, make the name an underscore
		if (current.isa(Token.VAR) || current.isa(Token.SYMBOL) || current.isa(Token.DOUBLE_COLON_BEFORE_SQUARE_BRACKET)) {
			Symbol var_name = SymbolConstants.UNDERSCORE;
			if (current.isa(Token.VAR)) {
				var_name = ((VarToken)current).getSymbol();
				current = tokens.next(); // Move on only if it is a VarToken
			}
			
			boolean copy = false;
			StaticBlock arg_type = null;
			
			// Copy?
			if (tokens.hasNext() && tokens.peek().isa(Token.OP) && tokens.peek().getData().equals("$")) {
				tokens.next(); // Discard $
				copy = true;
			}
		
			
			// Type annotation?
			// { a::[num]list ... }
			// { a::[[num]list]my_type ... }
			// { a::[module.point]module.list ... }
			if (tokens.hasNext() && (tokens.peek().isa(Token.DOUBLE_COLON_BEFORE_SQUARE_BRACKET) || tokens.peek().isa(Token.SYMBOL))) {
				ArrayList<Instruction> instructions = new ArrayList<Instruction>();
				
				// Collect instructions until we either hit the end or find the SECOND normal variable instruction
				// { a::[num]list b c , }
				// ...............^
				// { a::[[num]list]my_type , }
				// ........................^
				// { a::[module.point]module.foo.list b c , }
				// ...................................^
				
				if (tokens.peek().isa(Token.DOUBLE_COLON_BEFORE_SQUARE_BRACKET)) {
					tokens.next(); // Discard ::
				}
				
				// If the token is a symbol, convert it to a variable token
				if (tokens.peek().isa(Token.SYMBOL)) {
					SymbolToken symtoken = (SymbolToken)tokens.peek();
					tokens.replaceNext(new VarToken(symtoken.getSymbol().name(), symtoken.getSourceStringRef(), false));
				}
				
				boolean found_var_token = false;
				boolean done = false;
				while (tokens.hasNext() && !done) {
					current = tokens.peek();
					if (current.isa(Token.VAR)) {
						// Is this the second one we've seen?
						if (found_var_token == true) {
							// This is the second one we found, we are done
							done = true;
							break;
						} else {
							// This is only the first one we've seen
							found_var_token = true;
						}
					} else if (current.isa(Token.SYMBOL)) {
						done = true;
						break;
					} else if (current.isa(Token.DOUBLE_COLON_BEFORE_SQUARE_BRACKET)) {
						done = true;
						break;
					}
					instructions.add(current.getInstruction());
					tokens.next(); 
				}				
				Collections.reverse(instructions);
				InstructionStack is = new InstructionStack();
				is.addAll(instructions);
				arg_type = BlockUtils.fromIS(is);
			}
			
			// No type checking to do
			if (arg_type == null || !AyaPrefs.isTypeCheckerEnabled()) {
				if (copy) {
					return new CopyAssignment(current.getSourceStringRef(), var_name, true);
				} else {
					return new SimpleAssignment(current.getSourceStringRef(), var_name);
				}
			} else {
				return new TypedAssignment(current.getSourceStringRef(), var_name, arg_type, copy);
			}
			
		} else if (current.isa(Token.LIST)) {
			tokens.next(); // "accept" the peek
			ListToken unpack = (ListToken)current;
			TokenQueue tq = new TokenQueue(unpack.getCol());
			ArrayList<UnpackAssignment.Arg> args = new ArrayList<UnpackAssignment.Arg>();
			Symbol catchall = null;
			while (tq.hasNext()) {
				// Catch-all
				if (tq.peek().isa(Token.COLON)) {
					Token colon = tq.next(); // colon
					if (tq.hasNext() && tq.peek().isa(Token.VAR)) {
						Token var = tq.next();
						catchall = ((VarToken)var).getSymbol();
					} else {
						throw new SyntaxError("Expected varname after catchall assignment", colon.getSourceStringRef());
					}
					
					if (tq.hasNext()) {
						throw new SyntaxError("Catch-all name must be last", current.getSourceStringRef());
					}
				} else {
					Assignment a = nextArg(tq);
					boolean slurp = false;
					if (tq.hasNext() && tq.peek().isa(Token.OP) && tq.peek().getData().equals("~")) {
						slurp = true;
						tq.next(); // Skip ~
					}

					args.add(new UnpackAssignment.Arg(a, slurp));
				}
			}

			if (args.size() == 0) {
				throw new SyntaxError("Unpack args must contain at least one element", current.getSourceStringRef());
			} else {
				UnpackAssignment ua = UnpackAssignment.fromArgList(args, catchall, current.getSourceStringRef());
				return ua;
			}
		} else {
			throw new SyntaxError("All arguments should follow the format name[$][::type]", current.getSourceStringRef());
		}
	}
	
	/**
	 * 
	 * @param tokens
	 * @param has_arrow: If true, there is an arrow meaning that the return type is explicitly set to 0 items returned
	 * @return
	 * @throws EndOfInputError
	 * @throws ParserException
	 */
	private static CheckReturnTypeGenerator generateReturnTypes(TokenQueue tokens, boolean has_arrow) throws EndOfInputError, ParserException {
		if (tokens.size() == 0 ) {
			if (has_arrow) {
				return CheckReturnTypeGenerator.EMPTY;
			} else {
				return null;
			}
		} else {
			var out = new ArrayList<Pair<Symbol, StaticBlock>>();
			
			while (tokens.hasNext()) {
				// Use the argument parser
				// TODO: Should probably create an intermediate object for the output of nextArg
				//       instead of overloading Assignment like this
				Assignment arg;
				try {
					arg = nextArg(tokens);
				} catch (SyntaxError e) {
					throw new SyntaxError("Invalid return type syntax", e.getSource());

				}
				// Validate arg
				if (arg instanceof TypedAssignment) {
					var ta = (TypedAssignment)arg;
					if (ta.getCopy()) {
						throw new SyntaxError("Invalid return type syntax", ta.getSource());
					} else {
						out.add(new Pair<Symbol, StaticBlock>(ta.getVarName(), ta.getTypeBlock()));
					}
				} else if (arg instanceof SimpleAssignment) {
					throw new SyntaxError("Invalid return type syntax, must specify a type", arg.getSource());
				} else {
					throw new SyntaxError("Invalid return type syntax", arg.getSource());
				}
			}
			
			return new CheckReturnTypeGenerator(out);
		}
	}
	
	/** Assumes args have already been set 
	 * @param existing_names 
	 * @param captures 
	 * @throws ParserException */
	private static Pair<Dict, HashMap<Symbol, StaticBlock>> generateBlockHeaderDefaults(TokenQueue tokens, HashSet<Symbol> existing_names) throws ParserException {
		Dict locals = new Dict();
		HashMap<Symbol, StaticBlock> captures = new HashMap<Symbol, StaticBlock>();

		while (tokens.hasNext()) {
			Token current = tokens.next();
			if (current.isa(Token.VAR)) {
				VarToken var = (VarToken)current;
				if (!tokens.hasNext() || tokens.peek().isa(Token.VAR)) {
					Symbol name = var.getSymbol();
					checkDuplicate(existing_names, name, var.getSourceStringRef());
					locals.set(name, Num.ZERO);
				} else if (tokens.peek().isa(Token.LAMBDA)){
					LambdaToken lambda = (LambdaToken)tokens.next();
					Symbol name = var.getSymbol();
					checkDuplicate(existing_names, name, var.getSourceStringRef());
					captures.put(name, BlockUtils.fromIS(lambda.generateInstructionsForFirst()));
				} else if (tokens.peek().isa(Token.OP)) {
					OperatorToken opt = (OperatorToken)tokens.next();
					if (opt.getData().equals("^")) {
						Instruction i = new QuoteGetVariableInstruction(current.getSourceStringRef(), var.getSymbol());
						StaticBlock b = BlockUtils.makeBlockWithSingleInstruction(i);
						Symbol name = var.getSymbol();
						checkDuplicate(existing_names, name, var.getSourceStringRef());
						captures.put(name, b);
					} else {
						generateBlockHeaderDefaultsError(current.getSourceStringRef());
					}
				} else {
					generateBlockHeaderDefaultsError(current.getSourceStringRef());
				}
			} else {
				generateBlockHeaderDefaultsError(current.getSourceStringRef());
			}
		}
		
		return new Pair<Dict, HashMap<Symbol, StaticBlock>>(locals, captures);
	}
	
	private static void generateBlockHeaderDefaultsError(SourceStringRef source) throws SyntaxError {
		throw new SyntaxError("All variable initializers should follow the format name()", source);
	}
		
	
	
	/**
	 * 
	 * @param tokens
	 * @return (has_arrow, before_arrow, after_arrow)
	 * @throws SyntaxError
	 */
	private static Triple<Boolean, TokenQueue, TokenQueue> splitAtArrow(TokenQueue tokens) throws SyntaxError {
		ArrayList<Token> ts = tokens.getArrayList();
		for (int i = 0; i < ts.size(); i++) {
			if (ts.get(i).isa(Token.OP) && ts.get(i).getData().equals("-")) {
				if (i+1 < ts.size() 
						&& ts.get(i+1).isa(Token.OP) 
						&& ts.get(i+1).getData().equals(">")) {
					var pair = splitAtIndex(tokens, i+1); // Removes the `>`
					pair.first().popBack(); // Pop the `-`
					return new Triple<Boolean, TokenQueue, TokenQueue>(true, pair.first(), pair.second());
				} else {
					throw new SyntaxError("`-` should be followed by `>` in block header", ts.get(i).getSourceStringRef());
				}
			}
		}
		return new Triple<Boolean, TokenQueue, TokenQueue>(false, tokens, new TokenQueue());
	}
	
	/** Split a single tokenQueue into two at the location of the colon
	 * t1 contains all tokens before the colon
	 * t2 contains all tokens after the colon
	 * both may be empty if there was nothing before/after the colon
	 * the colon is not included in any
	 * @param tokens
	 * @return
	 * @throws SyntaxError 
	 */
	private static Pair<TokenQueue, TokenQueue> splitAtColon(TokenQueue tokens) throws SyntaxError {
		ArrayList<Token> ts = tokens.getArrayList();
		for (int i = 0; i < ts.size(); i++) {
			if (ts.get(i).isa(Token.COLON)) {
				return splitAtIndex(tokens, i);
			}
		}
		return new Pair<TokenQueue, TokenQueue>(tokens, new TokenQueue());		
	}
	
	private static Pair<TokenQueue, TokenQueue> splitAtIndex(TokenQueue tokens, int index) {
		ArrayList<Token> ts = tokens.getArrayList();
		ArrayList<Token> t1 = new ArrayList<Token>(index);
		ArrayList<Token> t2 = new ArrayList<Token>(ts.size()-index);
		for (int i = 0; i < index; i++) {
			t1.add(ts.get(i));
		}
		
		// colon_index+1 skip the index itself
		for (int i = index+1; i < ts.size(); i++) {
			t2.add(ts.get(i));
		}
		
		return new Pair<TokenQueue, TokenQueue>(new TokenQueue(t1),
												new TokenQueue(t2));
	}
	

	
}
