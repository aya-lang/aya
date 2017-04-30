package aya.parser.tokens;

import java.util.ArrayList;

import aya.entities.InstructionStack;
import aya.entities.Lambda;
import aya.entities.ListLiteral;
import aya.entities.Tuple;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.number.Num;
import aya.parser.Parser;
import aya.parser.token.TokenQueue;

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
			
			Lambda outLambda;
			
			//If contains only block, dump instructions
			if(lambdaIL.size() == 1
					&& lambdaIL.peek(0) instanceof Block
					&& ! (lambdaIL.peek(0) instanceof ListLiteral) ) {
				outLambda = new Lambda(((Block)(lambdaIL.peek(0))).getInstructions());
			} else {
				outLambda = new Lambda(lambdaIL);
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


	public boolean containsConst() {
		ArrayList<TokenQueue> lambdaData = getLambdaData();
		if (lambdaData.size() == 1) {
			InstructionStack is = Parser.generate(lambdaData.get(0));
			if (is.size() == 1 && is.peek(0) instanceof Obj) {
				return true;
			}
			
		}
		return false;
	}
	
	/** Use containsConst() first */
	public Obj getConstObj() {
		ArrayList<TokenQueue> lambdaData = getLambdaData();
		InstructionStack is = Parser.generate(lambdaData.get(0));
		Obj o = (Obj)is.pop();
		
		// If list literal, wrap it in a block so that it will be evaluated at run time 
		if (o instanceof ListLiteral) {
			Block b = new Block();
			b.add(o);
			return b;
		} else {
			return o;
		}
	}
}
