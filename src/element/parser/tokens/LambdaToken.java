package element.parser.tokens;

import java.math.BigDecimal;
import java.util.ArrayList;

import element.ElemTypes;
import element.entities.Block;
import element.entities.InstructionStack;
import element.entities.Lambda;
import element.entities.Tuple;
import element.parser.Parser;
import element.parser.token.TokenQueue;

public class LambdaToken extends CollectionToken {
		
	public LambdaToken(String data, ArrayList<Token> col) {
		super(Token.LAMBDA, data, col);
	}

	
	@Override
	public Object getElementObj() {
		
		//Is it a negative number?
		if(col.size() == 2 && col.get(0).isa(Token.OP)){
			//Is it the negation op?
			OperatorToken op = (OperatorToken) col.get(0);
			if(op.getOpType() == OperatorToken.STD && op.getData().charAt(0) == '-') {

				//Parse the number and negate it
				Token t = col.get(1);
				if(t.isa(Token.NUM)) {
					try {
						return Integer.parseInt(t.getData())*-1;
					} catch(NumberFormatException e) {
						return new BigDecimal(t.getData()).negate();
					}
				} 
			}
		}
		
		
		//Split Tokens where there are commas
		ArrayList<TokenQueue> lambdaData = splitCommas(col);
		if(lambdaData.size() == 1) {
			InstructionStack lambdaIL = Parser.generate(lambdaData.get(0));
			
			Lambda outLambda;
			
			//If contains only block, dump instructions
			if(lambdaIL.size() == 1 && ElemTypes.isBlock(lambdaIL.peek(0)) /* && !ElemTypes.toBlock(lambdaIL.peek(0)).isListLiteral() */) {
				outLambda = new Lambda(ElemTypes.toBlock(lambdaIL.peek(0)).getInstructions());
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
}
