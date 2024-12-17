package aya.parser.tokens;

import java.util.ArrayList;
import java.util.HashMap;

import aya.exceptions.parser.EndOfInputError;
import aya.exceptions.parser.ParserException;
import aya.exceptions.parser.SyntaxError;
import aya.instruction.BlockLiteralInstruction;
import aya.instruction.Instruction;
import aya.instruction.InstructionStack;
import aya.instruction.variable.QuoteGetVariableInstruction;
import aya.instruction.variable.assignment.Assignment;
import aya.instruction.variable.assignment.SimpleAssignment;
import aya.instruction.variable.assignment.TypedAssignment;
import aya.instruction.variable.assignment.UnpackAssignment;
import aya.obj.Obj;
import aya.obj.block.BlockUtils;
import aya.obj.block.StaticBlock;
import aya.obj.dict.Dict;
import aya.obj.number.Num;
import aya.obj.symbol.Symbol;
import aya.parser.Parser;
import aya.parser.SourceStringRef;
import aya.parser.token.TokenQueue;
import aya.util.Pair;
import aya.util.Triple;

public class BlockToken extends CollectionToken {
	
	public static final Obj DEFAULT_LOCAL_VAR = Num.ZERO;
		
	public BlockToken(String data, ArrayList<Token> col, SourceStringRef source) {
		super(Token.BLOCK, data, col, source);
	}

	
	@Override
	public Instruction getInstruction() throws ParserException {
		//Split Tokens where there are commas
		ArrayList<TokenQueue> blockData = splitCommas(col);
		if (blockData.size() == 1) {
			InstructionStack instructions = Parser.generate(blockData.get(0));
			return new BlockLiteralInstruction(this.getSourceStringRef(), BlockUtils.fromIS(instructions));
		} else {
			TokenQueue header = blockData.get(0);

			if (blockData.size() == 2) {
				//Empty header, dict literal
				if (!header.hasNext()) {
					throw new SyntaxError("Block cannot have an empty header", source);
					//InstructionStack instructions = Parser.generate(blockData.get(1));
					//if (instructions.size() == 0) {
					//	return EmptyDictLiteralInstruction.INSTANCE;
					//} else {
					//	return new DictLiteralInstruction(this.getSourceStringRef(), BlockUtils.fromIS(instructions));
					//}
				}
				// Single number in header, create a dict factory with a capture
				else if (header.size() == 1 && header.peek() instanceof NumberToken) {
					throw new SyntaxError("Block cannot have a number in header", source);
					/*
					NumberToken nt = (NumberToken)header.peek();
					int n = 0;
					try {
						n = nt.numValue().toInt();
					} catch (NumberFormatException e) {
						throw new SyntaxError(nt + " is not a valid number in the blockEvaluator header", nt.getSourceStringRef());
					}

					if (n < 1) {
						throw new SyntaxError("Cannot capture less than 1 elements from outer stack in a dict literal", nt.getSourceStringRef());
					}
					InstructionStack instructions = Parser.generate(blockData.get(1));
					if (n == 0 && instructions.isEmpty()) {
						return EmptyDictLiteralInstruction.INSTANCE;
					} else {
						return new DictLiteralInstruction(this.getSourceStringRef(), BlockUtils.fromIS(instructions), n);
					}
					*/
				}
				//Non-empty header, args and local variables
				else {
					InstructionStack main_instructions = Parser.generate(blockData.get(1));
					Triple<ArrayList<Assignment>, Dict, HashMap<Symbol, StaticBlock>> p = generateBlockHeader(blockData.get(0));
					StaticBlock blk = BlockUtils.fromIS(main_instructions, p.second(), p.first());
					return new BlockLiteralInstruction(this.getSourceStringRef(), blk, p.third());
				}
			} else {
				throw new SyntaxError("BlockEvaluator contains too many parts", getSourceStringRef());
			}
		}
	}
	
	// args, locals, captures
	private Triple<ArrayList<Assignment>, Dict, HashMap<Symbol, StaticBlock>> generateBlockHeader(TokenQueue tokens) throws ParserException {
		Pair<TokenQueue, TokenQueue> split_tokens = splitAtColon(tokens);
		TokenQueue arg_tokens = split_tokens.first();
		TokenQueue locals_and_captures_tokens = split_tokens.second();
	
		// Args
		ArrayList<Assignment> args = generateBlockHeaderArgs(arg_tokens);

		// Locals & Captures
		Pair<Dict, HashMap<Symbol, StaticBlock>> locals_and_captures = generateBlockHeaderDefaults(locals_and_captures_tokens);
		Dict locals = locals_and_captures.first();
		HashMap<Symbol, StaticBlock> captures = locals_and_captures.second();
		
		// Null checks
		if (args.size() == 0) args = null;
		if (locals.size() == 0) locals = null;
		if (captures.size() == 0) captures = null;

		return new Triple<ArrayList<Assignment>, Dict, HashMap<Symbol, StaticBlock>>(args, locals, captures);
	}
	
	private static ArrayList<Assignment> generateBlockHeaderArgs(TokenQueue tokens) throws ParserException {
		ArrayList<Assignment> out = new ArrayList<Assignment>();
		while (tokens.hasNext()) {
			Assignment arg = nextArg(tokens);
			out.add(arg);
		}
		return out;
	}
	
	private static Assignment nextArg(TokenQueue tokens) throws EndOfInputError, SyntaxError {
		Token current = tokens.next();
		if (current.isa(Token.VAR)) {
			VarToken var = (VarToken)current;
			boolean copy = false;
			Symbol arg_type = null;
			
			// Copy?
			if (tokens.hasNext() && tokens.peek().isa(Token.OP) && tokens.peek().data.equals("$")) {
				tokens.next(); // Discard $
				copy = true;
			}
		
			// Type annotation?
			if (tokens.hasNext() && tokens.peek().isa(Token.SYMBOL)) {
				SymbolToken sym_token = (SymbolToken)tokens.next();
				arg_type = sym_token.getSymbol();
			}
			
			if (copy || arg_type != null) {
				return new TypedAssignment(var.getSourceStringRef(), var.getSymbol(), arg_type, copy);
			} else {
				return new SimpleAssignment(var.getSourceStringRef(), var.getSymbol());
			}
		} else if (current.isa(Token.LIST)) {
			ListToken unpack = (ListToken)current;
			TokenQueue tq = new TokenQueue(unpack.col);
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
					if (tq.hasNext() && tq.peek().isa(Token.OP) && tq.peek().data.equals("~")) {
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
	
	/** Assumes args have already been set 
	 * @param captures 
	 * @throws ParserException */
	private static Pair<Dict, HashMap<Symbol, StaticBlock>> generateBlockHeaderDefaults(TokenQueue tokens) throws ParserException {
		Dict locals = new Dict();
		HashMap<Symbol, StaticBlock> captures = new HashMap<Symbol, StaticBlock>();

		while (tokens.hasNext()) {
			Token current = tokens.next();
			if (current.isa(VAR)) {
				VarToken var = (VarToken)current;
				if (!tokens.hasNext() || tokens.peek().isa(Token.VAR)) {
					locals.set(var.getSymbol(), Num.ZERO);
				} else if (tokens.peek().isa(Token.LAMBDA)){
					LambdaToken lambda = (LambdaToken)tokens.next();
					captures.put(var.getSymbol(), BlockUtils.fromIS(lambda.generateInstructionsForFirst()));
				} else if (tokens.peek().isa(Token.OP)) {
					OperatorToken opt = (OperatorToken)tokens.next();
					if (opt.data.equals("^")) {
						Instruction i = new QuoteGetVariableInstruction(current.getSourceStringRef(), var.getSymbol());
						StaticBlock b = BlockUtils.makeBlockWithSingleInstruction(i);
						captures.put(var.getSymbol(), b);
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
		int colons = 0;
		int colon_index = 0;
		for (int i = 0; i < ts.size(); i++) {
			if (ts.get(i).isa(Token.COLON)) {
				colon_index = i;
				colons++;
			}
		}
		
		if (colons == 0) {
			return new Pair<TokenQueue, TokenQueue>(tokens, new TokenQueue());
		} else if (colons > 1) {
			throw new SyntaxError("Expected only one colon (:) token in blockEvaluator header", tokens.peek().getSourceStringRef());
		} else {
			ArrayList<Token> t1 = new ArrayList<Token>(colon_index);
			ArrayList<Token> t2 = new ArrayList<Token>(ts.size()-colon_index);
			for (int i = 0; i < colon_index; i++) {
				t1.add(ts.get(i));
			}
			// colon_index+1 skip the colon itself
			for (int i = colon_index+1; i < ts.size(); i++) {
				t2.add(ts.get(i));
			}
			
			return new Pair<TokenQueue, TokenQueue>(new TokenQueue(t1),
													new TokenQueue(t2));
		}
	}
	

	@Override
	public String typeString() {
		return "block";
	}
}
