package aya.parser.tokens;

import java.util.ArrayList;

import aya.exceptions.parser.ParserException;
import aya.instruction.BlockLiteralInstruction;
import aya.instruction.Instruction;
import aya.instruction.InstructionStack;
import aya.instruction.LambdaInstruction;
import aya.instruction.TupleInstruction;
import aya.obj.block.BlockUtils;
import aya.obj.block.StaticBlock;
import aya.parser.Parser;
import aya.parser.SourceStringRef;
import aya.parser.token.TokenQueue;

public class LambdaToken extends CollectionToken {
	
	ArrayList<TokenQueue> _lambdaData;
	
	public LambdaToken(String data, ArrayList<Token> col, SourceStringRef source) {
		super(Token.LAMBDA, data, col, source);
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
			
			//If contains only blockEvaluator, set to auto eval
			BlockLiteralInstruction bli = lambdaIL.getIfSingleBlockInstruction();
			if (bli != null) {
				bli.setAutoEval();
				return bli;
			} else {
				return new LambdaInstruction(this.getSourceStringRef(), lambdaIL);
			}
		} else {
			StaticBlock[] elements = new StaticBlock[lambdaData.size()];
			for (int k = 0; k < elements.length; k++) {
				elements[k] = BlockUtils.fromIS(Parser.generate(lambdaData.get(k)));
			}
			return new TupleInstruction(this.getSourceStringRef(), elements);
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

}
