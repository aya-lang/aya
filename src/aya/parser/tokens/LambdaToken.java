package aya.parser.tokens;

import java.util.ArrayList;

import aya.Aya;
import aya.entities.EmptyListLiteral;
import aya.entities.InstructionStack;
import aya.entities.ListLiteral;
import aya.entities.Tuple;
import aya.instruction.EmptyDictLiteralInstruction;
import aya.instruction.LambdaInstruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.number.Num;
import aya.parser.Parser;
import aya.parser.token.TokenQueue;
import aya.util.Pair;
import aya.variable.Variable;

public class LambdaToken extends CollectionToken {
	
	ArrayList<TokenQueue> _lambdaData;
	
	public LambdaToken(String data, ArrayList<Token> col) {
		super(Token.LAMBDA, data, col);
	}

	private ArrayList<TokenQueue> getLambdaData() {
		if (_lambdaData == null) {
			_lambdaData = splitCommas(col);
		}
		return _lambdaData;
	}
	
	@Override
	public Object getAyaObj() {
		
		//Is it a negative number?
		if(col.size() == 2 && col.get(0).isa(Token.OP)){
			//Is it the negation op?
			OperatorToken op = (OperatorToken) col.get(0);
			if(op.getOpType() == OperatorToken.STD_OP && op.getData().charAt(0) == '-') {

				//Parse the number and negate it
				Token t = col.get(1);
				if(t.isa(Token.NUMERIC)) {
					return new Num(Double.parseDouble(t.getData())*-1);
				} 
			}
		}
		
		
		//Split Tokens where there are commas
		ArrayList<TokenQueue> lambdaData = getLambdaData();
		if(lambdaData.size() == 1) {
			InstructionStack lambdaIL = Parser.generate(lambdaData.get(0));
			
			LambdaInstruction outLambda;
			
			//If contains only block, dump instructions
			if(lambdaIL.size() == 1
					&& lambdaIL.peek(0) instanceof Block
					&& ! (lambdaIL.peek(0) instanceof ListLiteral) ) {
				outLambda = new LambdaInstruction(((Block)(lambdaIL.peek(0))).getInstructions());
			} else {
				outLambda = new LambdaInstruction(lambdaIL);
			}
			return outLambda;
		} else {
			Block[] elements = new Block[lambdaData.size()];
			for (int k = 0; k < elements.length; k++) {
				elements[k] = new Block(Parser.generate(lambdaData.get(k)));
			}
			return new Tuple(elements);
		}
	}
	
	

	@Override
	public String typeString() {
		return "block";
	}

	/**
	 * Should copy in init will only be false if the value is a variable reference
	 * @return Should copy on init, Aya object
	 */
	public Pair<Boolean, Obj> getInnerConstObj() {
		ArrayList<TokenQueue> lambdaData = getLambdaData();
		InstructionStack is = Parser.generate(lambdaData.get(0));
		if (is.size() != 1) return null;

		final Object o = is.pop();
		
		if (o instanceof EmptyListLiteral) {
			return new Pair<Boolean, Obj>(true, EmptyListLiteral.INSTANCE.getListCopy());
		} else if (o instanceof EmptyDictLiteralInstruction) {
			return new Pair<Boolean, Obj>(true, EmptyDictLiteralInstruction.INSTANCE.getDict());
		} else if (o instanceof Variable) {
			return new Pair<Boolean, Obj>(false, Aya.getInstance().getVars().getVar((Variable)o));
		} else if (o instanceof Obj) {
			return new Pair<Boolean, Obj>(true, (Obj)o);
		} else {
			return new Pair<Boolean, Obj>(false, null);
		}
	}
}
