package aya.parser.tokens;

import java.util.ArrayList;

import aya.exceptions.SyntaxError;
import aya.instruction.DataInstruction;
import aya.instruction.DictLiteralInstruction;
import aya.instruction.EmptyDictLiteralInstruction;
import aya.instruction.Instruction;
import aya.instruction.flag.PopVarFlagInstruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.block.BlockHeader;
import aya.obj.number.Num;
import aya.parser.Parser;
import aya.parser.token.TokenQueue;
import aya.util.Pair;

public class BlockToken extends CollectionToken {
	
	public static final Obj DEFAULT_LOCAL_VAR = new Num(0);
		
	public BlockToken(String data, ArrayList<Token> col) {
		super(Token.BLOCK, data, col);
	}

	
	@Override
	public Instruction getInstruction() {
		//Split Tokens where there are commas
		ArrayList<TokenQueue> blockData = splitCommas(col);
		switch(blockData.size()) {
		case 1:
			return new DataInstruction(new Block(Parser.generate(blockData.get(0))));
		case 2:
			TokenQueue header = blockData.get(0);
			//Empty header, dict literal
			if (!header.hasNext()) {
				Block b = new Block();
				b.addAll(Parser.generate(blockData.get(1)).getInstrucionList());
				if (b.isEmpty()) {
					return EmptyDictLiteralInstruction.INSTANCE;
				} else {
					return new DictLiteralInstruction(b);
				}
			}
			// Single number in header, create a dict factory with a capture
			else if (header.size() == 1 && header.peek() instanceof NumberToken) {
				NumberToken nt = (NumberToken)header.peek();
				int n = 0;
				try {
					n = nt.numValue().toInt();
				} catch (NumberFormatException e) {
					throw new SyntaxError(nt + " is not a valid number in the block header");
				}

				if (n < 0) {
					throw new SyntaxError("Cannot capture a negative number of elements in a dict literal");
				}
				Block b = new Block();
				b.addAll(Parser.generate(blockData.get(1)).getInstrucionList());
				if (n == 0 && b.isEmpty()) {
					return EmptyDictLiteralInstruction.INSTANCE;
				} else {
					return new DictLiteralInstruction(b, n);
				}
			}
			//Non-empty header, args and local variables
			else {
				Block b = new Block();
				b.add(PopVarFlagInstruction.INSTANCE);
				b.addAll(Parser.generate(blockData.get(1)).getInstrucionList());	//Main instructions
				BlockHeader block_header = generateBlockHeader(blockData.get(0));
				b.add(block_header);
				return new DataInstruction(b);
			}
		default:
			throw new SyntaxError("Block " + data + " contains too many parts");
		}
	}
	
	private BlockHeader generateBlockHeader(TokenQueue tokens) {
		BlockHeader header = new BlockHeader();
		
		Pair<TokenQueue, TokenQueue> split_tokens = splitAtColon(tokens);
		TokenQueue arg_tokens = split_tokens.first();
		TokenQueue default_tokens = split_tokens.second();
	
		generateBlockHeaderArgs(header, arg_tokens);
		generateBlockHeaderDefaults(header, default_tokens);
		
		return header;
	}
	
	private static void generateBlockHeaderArgs(BlockHeader header, TokenQueue tokens) {
		String orig = tokens.toString(); // For error reporting
		while (tokens.hasNext()) {
			Token current = tokens.next();
			if (current.isa(Token.VAR)) {
				VarToken var = (VarToken)current;
				BlockHeader.Arg arg = new BlockHeader.Arg(var.getID());
				
				if (tokens.hasNext()) {
					// Copy?
					if (tokens.peek().isa(Token.OP) && tokens.peek().data.equals("$")) {
						tokens.next(); // Discard $
						arg.copy = true;
					}
				
					// Type annotation?
					if (tokens.peek().isa(Token.SYMBOL)) {
						SymbolToken sym_token = (SymbolToken)tokens.next();
						arg.type = sym_token.getID();
					}
				}
				
				header.addArg(arg);
			} else {
				throw new SyntaxError("All arguments should follow the format name[$][::type].\n" +
									  "Got: " + orig);
			}
		}
	}
	
	/** Assumes args have already been set */
	private static void generateBlockHeaderDefaults(BlockHeader header, TokenQueue tokens) {
		String orig = tokens.toString();
		while (tokens.hasNext()) {
			Token current = tokens.next();
			if (current.isa(VAR)) {
				if (!tokens.hasNext() || tokens.peek().isa(Token.VAR)) {
					VarToken var = (VarToken)current;
					header.addDefault(var.getID(), Num.ZERO);
				} else if (tokens.peek().isa(Token.LAMBDA)){
					VarToken var = (VarToken)current;
					LambdaToken lambda = (LambdaToken)tokens.next();
					header.addDefault(var.getID(), lambda.generateInstructionsForFirst());
				} else {
					throw new SyntaxError("All variable initializers should follow the format name().\n" + "Got: " + orig);
				}
			} else {
				throw new SyntaxError("All variable initializers should follow the format name().\n" + "Got: " + orig);
			}
		}
	}
		
	
	
	/** Split a single tokenQueue into two at the location of the colon
	 * t1 contains all tokens before the colon
	 * t2 contains all tokens after the colon
	 * both may be empty if there was nothing before/after the colon
	 * the colon is not included in any
	 * @param tokens
	 * @return
	 */
	private static Pair<TokenQueue, TokenQueue> splitAtColon(TokenQueue tokens) {
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
			throw new SyntaxError("Expected only one colon (:) token in block header.\n" +
								  "Got: " + tokens.toString());
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
	
	/** Parses variable set */
	/*public static VariableSet parseVariableSet(ArrayList<Token> tokens) {
		//Validate arguments
		if(tokens.size() == 0) {
			throw new SyntaxError("Variable names not found in block");
		}
		
		//All vars, no need to check for type assertions
		if(allVars(tokens)) {
			//Set up arguments
			Variable[] args = new Variable[tokens.size()];
			for (int i = 0; i < args.length; i++) {
				final Token t = tokens.get(i);
				final GetVariableInstruction v = (GetVariableInstruction)(t.getInstruction());
				args[i] = new Variable(v.getID());
			}
			return new VariableSet(args,  null, null);
		} 
		
		//Contains Local Variables
		else if (blockHeaderHasLocals(tokens)) {
			ArrayList<Token> before_colon = new ArrayList<Token>();
			
			int size = tokens.size();
			for (int g = 0; g < size; g++) {
				//g is not the index, since we remove every time, use 0
				if (tokens.get(0).isa(Token.COLON)) {
					tokens.remove(0);
					break;
				} else {
					before_colon.add(tokens.get(0));
					tokens.remove(0);
				}
			}
			
			//Create the set
			VariableSet args;
			if (before_colon.size() != 0) {
				args = parseVariableSet(before_colon);
			} else {
				args = new VariableSet(null, null, null);
			}
			
			//Initialize local variables in the set
			Variable last = null;
			for (Token t : tokens) {

				if (t.isa(Token.LAMBDA) && last != null) {
					LambdaToken lt = (LambdaToken)t;
					Pair<Boolean, Obj> inner_obj = lt.getInnerConstObj();
					Obj obj = inner_obj.second();
					if (obj == null) {
						throw new SyntaxError("Block header: Local Variables Initializer: Must contain only const values");
					}
					args.setVar(last, obj);
					if (inner_obj.first()) args.copyOnInit(last);
					last = null;
				} else if(!t.isa(VAR)) {
					throw new SyntaxError("Block header: Local Variables: Must contain only variable names or"
							+ " initializers. Recieved:\n" + t.data);
				} else {
					last = new Variable(t.getData());
					args.setVar(last, DEFAULT_LOCAL_VAR);
					// Note: Variables without an explicit item will not be added to the copyOnInit list
				}
			}
			return args;
		}
		
		//Things other than vars exist, check if they are type assertions
		else {
			ArrayList<Variable> argNames = new ArrayList<Variable>();
			ArrayList<Long> argTypes = new ArrayList<Long>();
			for (int i = 0; i < tokens.size(); i++) {
				if (tokens.get(i).getType() == Token.VAR) {
					if (i+1 > tokens.size()-1) {
						argNames.add(new Variable(tokens.get(i).getData()));
						argTypes.add(Obj.SYM_ANY.id());
					} else if (tokens.get(i+1).getType() == Token.SYMBOL) {
						argNames.add(new Variable(tokens.get(i).getData()));
						Symbol s = Symbol.fromStr(tokens.get(i+1).getData());
						argTypes.add(s.id());
						i++; //Skip the symbol on the next iteration
					} else if (tokens.get(i).getType() == Token.VAR) {
						argNames.add(new Variable(tokens.get(i).getData()));
						argTypes.add(Obj.SYM_ANY.id());
					} else {
						//Should always be a VAR or an SYMBOL
						throw new SyntaxError("All arguments must be names or type assertions. Recieved " + tokens.get(i).data);
					}
				} else {
					//Should always be a VAR or an SYMBOL
					throw new SyntaxError("All arguments must be names or type assertions. Recieved " + tokens.get(i).data );
				}
			}
			
			//Convert to primitive byte array
			boolean allAny = true;
			long[] types = new long[argTypes.size()];
			for (int i = 0; i < types.length; i++) {
				types[i] = argTypes.get(i);
				if(types[i] != Obj.SYM_ANY.id()) {
					allAny = false;
				}
			}
			
			//Improves runtime speed. No need to check if they are all ANY
			if(allAny) {
				types = null;
			}
			
			return new VariableSet(argNames.toArray(new Variable[argNames.size()]), types, null);
		}
	}
	*/
	
	/** Returns true if there exists a single colon in the header
	 * Throws a syntax error if there are more than one 
	 * returns false if there are zero colons in the header */
	/*public static boolean blockHeaderHasLocals(ArrayList<Token> tokens) {
		int colons = 0;
		for (Token t : tokens) {
			if (t.isa(Token.COLON)) {
				colons++;
			}
		}
		
		//Too many colons
		if (colons > 1){ 
			String tstr = "";
			for (Token t : tokens) {
				tstr += t.toString();
			}
			throw new SyntaxError("Too many colons in block header: {" + tstr + ", ... }");
		}
		// Zero or one colons
		else {
			return colons == 1;
		}
	}*/
	
	/** checks if every token is a variable token */
	/*public static boolean allVars(ArrayList<Token> tokens) {
		for (Token t : tokens) {
			if (!t.isa(Token.VAR)) {
				return false;
			}
		}
		return true;
	}*/

	@Override
	public String typeString() {
		return "block";
	}
}
