package aya.parser.tokens;

import java.util.ArrayList;

import aya.exceptions.SyntaxError;
import aya.instruction.VariableSetInstruction;
import aya.instruction.flag.PopVarFlagInstruction;
import aya.instruction.variable.GetVariableInstruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.dict.DictFactory;
import aya.obj.dict.EmptyDictFactory;
import aya.obj.number.Num;
import aya.obj.symbol.Symbol;
import aya.parser.Parser;
import aya.parser.token.TokenQueue;
import aya.util.Pair;
import aya.variable.Variable;
import aya.variable.VariableSet;

public class BlockToken extends CollectionToken {
	
	public static final Obj DEFAULT_LOCAL_VAR = new Num(0);
		
	public BlockToken(String data, ArrayList<Token> col) {
		super(Token.BLOCK, data, col);
	}

	
	@Override
	public Object getAyaObj() {
		//Split Tokens where there are commas
		ArrayList<TokenQueue> blockData = splitCommas(col);
		switch(blockData.size()) {
		case 1:
			return new Block(Parser.generate(blockData.get(0)));
		case 2:
			TokenQueue header = blockData.get(0);
			//Empty header, dict literal
			if (!header.hasNext()) {
				Block b = new Block();
				b.addAll(Parser.generate(blockData.get(1)).getInstrucionList());
				if (b.isEmpty()) {
					return EmptyDictFactory.INSTANCE;
				} else {
					return new DictFactory(b);
				}
			}
			// Single number in header, create a dict factory with a capture
			else if (header.size() == 1 && header.peek() instanceof NumberToken) {
				int n = ((Num)header.peek().getAyaObj()).toInt();
				if (n < 0) {
					throw new SyntaxError("Cannot capture a negative number of elements in a dict literal");
				}
				Block b = new Block();
				b.addAll(Parser.generate(blockData.get(1)).getInstrucionList());
				if (n == 0 && b.isEmpty()) {
					return EmptyDictFactory.INSTANCE;
				} else {
					return new DictFactory(b, n);
				}
			}
			//Non-empty header, args and local variables
			else {
				Block b = new Block();
				b.add(PopVarFlagInstruction.INSTANCE);
				b.addAll(Parser.generate(blockData.get(1)).getInstrucionList());	//Main instructions
				VariableSet header_vars = parseVariableSet(blockData.get(0).getArrayList());	//Block arguments
				b.add(new VariableSetInstruction(header_vars));
				return b;
			}
		default:
			throw new SyntaxError("Block " + data + " contains too many parts");
		}
	}
	
	/** Parses variable set */
	public static VariableSet parseVariableSet(ArrayList<Token> tokens) {
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
				final GetVariableInstruction v = (GetVariableInstruction)(t.getAyaObj());
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
	
	/** Returns true if there exists a single colon in the header
	 * Throws a syntax error if there are more than one 
	 * returns false if there are zero colons in the header */
	public static boolean blockHeaderHasLocals(ArrayList<Token> tokens) {
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
	}
	
	/** checks if every token is a variable token */
	public static boolean allVars(ArrayList<Token> tokens) {
		for (Token t : tokens) {
			if (!t.isa(Token.VAR)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String typeString() {
		return "block";
	}
}
