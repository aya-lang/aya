package aya.parser.tokens;

import java.util.ArrayList;
import java.util.HashMap;

import aya.exceptions.ex.ParserException;
import aya.exceptions.ex.SyntaxError;
import aya.instruction.BlockLiteralInstruction;
import aya.instruction.DictLiteralInstruction;
import aya.instruction.EmptyDictLiteralInstruction;
import aya.instruction.Instruction;
import aya.instruction.flag.PopVarFlagInstruction;
import aya.instruction.variable.QuoteGetVariableInstruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.block.BlockHeader;
import aya.obj.block.BlockHeaderArg;
import aya.obj.number.Num;
import aya.obj.symbol.Symbol;
import aya.parser.Parser;
import aya.parser.SourceStringRef;
import aya.parser.token.TokenQueue;
import aya.util.Pair;

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
			return new BlockLiteralInstruction(this.getSourceStringRef(), new Block(Parser.generate(blockData.get(0))));
		} else {
			TokenQueue header = blockData.get(0);

			if (blockData.size() == 2) {
				//Empty header, dict literal
				if (!header.hasNext()) {
					Block b = new Block();
					b.addAll(Parser.generate(blockData.get(1)).getInstrucionList());
					if (b.isEmpty()) {
						return EmptyDictLiteralInstruction.INSTANCE;
					} else {
						return new DictLiteralInstruction(this.getSourceStringRef(), b);
					}
				}
				// Single number in header, create a dict factory with a capture
				else if (header.size() == 1 && header.peek() instanceof NumberToken) {
					NumberToken nt = (NumberToken)header.peek();
					int n = 0;
					try {
						n = nt.numValue().toInt();
					} catch (NumberFormatException e) {
						throw new SyntaxError(nt + " is not a valid number in the block header", nt.getSourceStringRef());
					}

					if (n < 1) {
						throw new SyntaxError("Cannot capture less than 1 elements from outer stack in a dict literal", nt.getSourceStringRef());
					}
					Block b = new Block();
					b.addAll(Parser.generate(blockData.get(1)).getInstrucionList());
					if (n == 0 && b.isEmpty()) {
						return EmptyDictLiteralInstruction.INSTANCE;
					} else {
						return new DictLiteralInstruction(this.getSourceStringRef(), b, n);
					}
				}
				//Non-empty header, args and local variables
				else {
					Block b = new Block();
					b.add(PopVarFlagInstruction.INSTANCE);
					b.addAll(Parser.generate(blockData.get(1)).getInstrucionList());	//Main instructions
					Pair<BlockHeader, HashMap<Symbol, Block>> p = generateBlockHeader(blockData.get(0));
					BlockHeader block_header = p.first();
					HashMap<Symbol, Block> captures = p.second();
					b.add(block_header);
					return new BlockLiteralInstruction(this.getSourceStringRef(), b, captures);
				}
			} else {
				throw new SyntaxError("Block contains too many parts", getSourceStringRef());
			}
		}
	}
	
	private Pair<BlockHeader, HashMap<Symbol, Block>> generateBlockHeader(TokenQueue tokens) throws ParserException {
		BlockHeader header = new BlockHeader(this.getSourceStringRef());
		
		Pair<TokenQueue, TokenQueue> split_tokens = splitAtColon(tokens);
		TokenQueue arg_tokens = split_tokens.first();
		TokenQueue default_tokens = split_tokens.second();
		HashMap<Symbol, Block> captures = new HashMap<Symbol, Block>();
	
		generateBlockHeaderArgs(header, arg_tokens);
		generateBlockHeaderDefaults(header, default_tokens, captures);
		
		return new Pair<BlockHeader, HashMap<Symbol, Block>>(header, captures);
	}
	
	private static void generateBlockHeaderArgs(BlockHeader header, TokenQueue tokens) throws ParserException {
		String orig = tokens.toString(); // For error reporting
		while (tokens.hasNext()) {
			Token current = tokens.next();
			if (current.isa(Token.VAR)) {
				VarToken var = (VarToken)current;
				BlockHeaderArg arg = new BlockHeaderArg(var.getSymbol());
				
				// Copy?
				if (tokens.hasNext() && tokens.peek().isa(Token.OP) && tokens.peek().data.equals("$")) {
					tokens.next(); // Discard $
					arg.copy = true;
				}
			
				// Type annotation?
				if (tokens.hasNext() && tokens.peek().isa(Token.SYMBOL)) {
					SymbolToken sym_token = (SymbolToken)tokens.next();
					arg.type = sym_token.getSymbol();
				}
				
				header.addArg(arg);
			} else {
				throw new SyntaxError("All arguments should follow the format name[$][::type].\n" +
									  "Got: " + orig, current.getSourceStringRef());
			}
		}
	}
	
	/** Assumes args have already been set 
	 * @param captures 
	 * @throws ParserException */
	private static void generateBlockHeaderDefaults(BlockHeader header, TokenQueue tokens, HashMap<Symbol, Block> captures) throws ParserException {
		String orig = tokens.toString();
		while (tokens.hasNext()) {
			Token current = tokens.next();
			if (current.isa(VAR)) {
				VarToken var = (VarToken)current;
				if (!tokens.hasNext() || tokens.peek().isa(Token.VAR)) {
					header.addDefault(var.getSymbol(), Num.ZERO);
				} else if (tokens.peek().isa(Token.LAMBDA)){
					LambdaToken lambda = (LambdaToken)tokens.next();
					captures.put(var.getSymbol(), new Block(lambda.generateInstructionsForFirst()));
				} else if (tokens.peek().isa(Token.OP)) {
					OperatorToken opt = (OperatorToken)tokens.next();
					if (opt.data.equals("^")) {
						Block b = new Block();
						b.add(new QuoteGetVariableInstruction(current.getSourceStringRef(), var.getSymbol()));
						captures.put(var.getSymbol(), b);
					} else {
						generateBlockHeaderDefaultsError(orig, current.getSourceStringRef());
					}
				} else {
					generateBlockHeaderDefaultsError(orig, current.getSourceStringRef());
				}
			} else {
				generateBlockHeaderDefaultsError(orig, current.getSourceStringRef());
			}
		}
	}
	
	private static void generateBlockHeaderDefaultsError(String orig, SourceStringRef source) throws SyntaxError {
		throw new SyntaxError("All variable initializers should follow the format name().\n" + "Got: " + orig, source);
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
			throw new SyntaxError("Expected only one colon (:) token in block header", tokens.peek().getSourceStringRef());
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
