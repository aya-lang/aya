package aya.parser.tokens;

import java.util.ArrayList;

import aya.Aya;
import aya.exceptions.ex.ParserException;
import aya.instruction.BlockLiteralInstruction;
import aya.instruction.DataInstruction;
import aya.instruction.EmptyDictLiteralInstruction;
import aya.instruction.EmptyListLiteralInstruction;
import aya.instruction.Instruction;
import aya.instruction.InstructionStack;
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
	public Instruction getInstruction() throws ParserException {

		//Split Tokens where there are commas
		ArrayList<TokenQueue> lambdaData = getLambdaData();
		if(lambdaData.size() == 1) {
			InstructionStack lambdaIL = Parser.generate(lambdaData.get(0));
			
			//If contains only block, set to auto eval
			BlockLiteralInstruction bli = lambdaIL.getIfSingleBlockInstruction();
			if (bli != null) {
				bli.setAutoEval();
				return bli;
			} else {
				return new LambdaInstruction(lambdaIL);
			}
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
	
	/** Does not generate instructions for sections after the first (if they exist) 
	 * @throws ParserException */
	public InstructionStack generateInstructionsForFirst() throws ParserException {
		ArrayList<TokenQueue> lambdaData = getLambdaData();
		return Parser.generate(lambdaData.get(0));
	}

	/**
	 * Should copy in init will only be false if the value is a variable reference
	 * @throws ParserException 
	 */
	public Pair<Boolean, Obj> getInnerConstObj() throws ParserException {
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
			return new Pair<Boolean, Obj>(false, Aya.getInstance().getVars().getVar(var.getSymbol()));
		} else if (o instanceof DataInstruction) {
			return new Pair<Boolean, Obj>(true, ((DataInstruction)o).getData());
		} else {
			return new Pair<Boolean, Obj>(false, null);
		}
	}
}
