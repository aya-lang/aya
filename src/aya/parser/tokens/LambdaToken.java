package aya.parser.tokens;

import java.util.ArrayList;

import aya.Aya;
import aya.entities.InstructionStack;
import aya.instruction.DataInstruction;
import aya.instruction.EmptyDictLiteralInstruction;
import aya.instruction.EmptyListLiteralInstruction;
import aya.instruction.Instruction;
import aya.instruction.LambdaInstruction;
import aya.instruction.TupleInstruction;
import aya.instruction.variable.GetVariableInstruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.number.Num;
import aya.parser.Parser;
import aya.parser.token.TokenQueue;
import aya.util.Pair;

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
	public Instruction getInstruction() {
		
		//Is it a negative number?
		if(col.size() == 2 && col.get(0).isa(Token.OP)){
			//Is it the negation op?
			OperatorToken op = (OperatorToken) col.get(0);
			if(op.getOpType() == OperatorToken.STD_OP && op.getData().charAt(0) == '-') {

				//Parse the number and negate it
				Token t = col.get(1);
				if(t.isa(Token.NUMERIC)) {
					return new DataInstruction(new Num(Double.parseDouble(t.getData())*-1));
				} 
			}
		}
		
		
		//Split Tokens where there are commas
		ArrayList<TokenQueue> lambdaData = getLambdaData();
		if(lambdaData.size() == 1) {
			InstructionStack lambdaIL = Parser.generate(lambdaData.get(0));
			
			LambdaInstruction outLambda;
			
			//If contains only block, dump instructions
			if (lambdaIL.size() == 1
					&& lambdaIL.peek(0) instanceof DataInstruction
					&& (((DataInstruction)lambdaIL.peek(0)).objIsa(Obj.BLOCK))) {
				DataInstruction data = (DataInstruction)lambdaIL.peek(0);
				outLambda = new LambdaInstruction(((Block)(data.getData())).getInstructions());
			} else {
				outLambda = new LambdaInstruction(lambdaIL);
			}
			return outLambda;
		} else {
			Block[] elements = new Block[lambdaData.size()];
			for (int k = 0; k < elements.length; k++) {
				elements[k] = new Block(Parser.generate(lambdaData.get(k)));
			}
			return new TupleInstruction(elements);
		}
	}
	
	

	@Override
	public String typeString() {
		return "block";
	}
	
	/** Does not generate instructions for sections after the first (if they exist) */
	public InstructionStack generateInstructionsForFirst() {
		ArrayList<TokenQueue> lambdaData = getLambdaData();
		return Parser.generate(lambdaData.get(0));
	}

	/**
	 * Should copy in init will only be false if the value is a variable reference
	 */
	public Pair<Boolean, Obj> getInnerConstObj() {
		ArrayList<TokenQueue> lambdaData = getLambdaData();
		InstructionStack is = Parser.generate(lambdaData.get(0));
		if (is.size() != 1) return null;

		final Instruction o = is.pop();
		
		if (o instanceof EmptyListLiteralInstruction) {
			return new Pair<Boolean, Obj>(true, EmptyListLiteralInstruction.INSTANCE.getListCopy());
		} else if (o instanceof EmptyDictLiteralInstruction) {
			return new Pair<Boolean, Obj>(true, EmptyDictLiteralInstruction.INSTANCE.getDict());
		} else if (o instanceof GetVariableInstruction) {
			GetVariableInstruction var = (GetVariableInstruction)o;
			return new Pair<Boolean, Obj>(false, Aya.getInstance().getVars().getVar(var.getID()));
		} else if (o instanceof DataInstruction) {
			return new Pair<Boolean, Obj>(true, ((DataInstruction)o).getData());
		} else {
			return new Pair<Boolean, Obj>(false, null);
		}
	}
}
