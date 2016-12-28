package element.parser.tokens;

import java.util.ArrayList;

import element.entities.Flag;
import element.exceptions.SyntaxError;
import element.obj.Obj;
import element.obj.block.Block;
import element.obj.dict.DictFactory;
import element.obj.number.Num;
import element.parser.Parser;
import element.parser.token.TokenQueue;
import element.variable.Variable;
import element.variable.VariableSet;

public class BlockToken extends CollectionToken {
	
	public static final Obj DEFAULT_LOCAL_VAR = new Num(0);
		
	public BlockToken(String data, ArrayList<Token> col) {
		super(Token.BLOCK, data, col);
	}

	
	@Override
	public Object getElementObj() {
		//Split Tokens where there are commas
		ArrayList<TokenQueue> blockData = splitCommas(col);
		switch(blockData.size()) {
		case 1:
			return new Block(Parser.generate(blockData.get(0)));
		case 2:
			TokenQueue header = blockData.get(0);
			if (!header.hasNext()) { //header.hasNext() && header.peek().isa(Token.OP) && header.peek().data.equals("@")) {
				//header.next();
				//if (header.size() > 1) { throw new SyntaxError("Must include only module name in header"); }
				//String modname = header.next().data;
				Block b = new Block();
				b.addAll(Parser.generate(blockData.get(1)).getInstrucionList());
				return new DictFactory(b);
			}			
			else {
				Block b = new Block();
				b.add(Flag.getFlag(Flag.POPVAR)); //Pop the local variables when the block is finished
				b.addAll(Parser.generate(blockData.get(1)).getInstrucionList());	//Main instructions
				b.add(parseVariableSet(blockData.get(0).getArrayList()));	//Block arguments
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
				args[i] = (Variable) tokens.get(i).getElementObj();
			}
			return new VariableSet(args,  null);
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
				args = new VariableSet(null, null);
			}
			
			//Initialize local variables in the set
			for (Token t : tokens) {
				if(!t.isa(Token.VAR)) {
					throw new SyntaxError("Block header: Local Variables: Must contain only variable names");
				} else {
					args.setVar(new Variable(t.getData()), DEFAULT_LOCAL_VAR);
				}
			}
			return args;
		}
		
		//Things other than vars exist, check if they are type assertions
		else {
			ArrayList<Variable> argNames = new ArrayList<Variable>();
			ArrayList<Byte> argTypes = new ArrayList<Byte>();
			for (int i = 0; i < tokens.size(); i++) {
				if (tokens.get(i).getType() == Token.VAR) {
					if (i+1 > tokens.size()-1) {
						argNames.add(new Variable(tokens.get(i).getData()));
						argTypes.add(Obj.ANY);
					} else if (tokens.get(i+1).getType() == Token.OP) {
						argNames.add(new Variable(tokens.get(i).getData()));
						char c = tokens.get(i+1).getData().charAt(0);
						argTypes.add(Obj.abbrvToID(c));
						i++; //Skip the op on the next iteration
					} else if (tokens.get(i).getType() == Token.VAR) {
						argNames.add(new Variable(tokens.get(i).getData()));
						argTypes.add(Obj.ANY);
					} else {
						//Should always be a VAR or an OP
						throw new SyntaxError("All arguments must be names or type assertions");
					}
				} else {
					//Should always be a VAR or an OP
					throw new SyntaxError("All arguments must be names or type assertions");
				}
			}
			
			//Convert to primitive byte array
			boolean allAny = true;
			byte[] types = new byte[argTypes.size()];
			for (int i = 0; i < types.length; i++) {
				types[i] = argTypes.get(i).byteValue();
				if(types[i] != Obj.ANY) {
					allAny = false;
				}
			}
			
			//Improves runtime speed. No need to check if they are all ANY
			if(allAny) {
				types = null;
			}
			
			return new VariableSet(argNames.toArray(new Variable[argNames.size()]), types);
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
