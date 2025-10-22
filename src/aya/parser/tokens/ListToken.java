package aya.parser.tokens;

import java.util.ArrayList;

import aya.exceptions.parser.ParserException;
import aya.exceptions.parser.SyntaxError;
import aya.instruction.BlockLiteralInstruction;
import aya.instruction.EmptyListLiteralInstruction;
import aya.instruction.Instruction;
import aya.instruction.InstructionStack;
import aya.instruction.ListBuilderInstruction;
import aya.instruction.ListLiteralInstruction;
import aya.obj.block.BlockUtils;
import aya.obj.block.StaticBlock;
import aya.parser.Parser;
import aya.parser.SourceStringRef;
import aya.parser.token.TokenQueue;

public class ListToken extends CollectionToken {
	
	public ListToken(String data, ArrayList<Token> col, SourceStringRef source) {
		super(Token.LIST, data, col, source);
	}

	
	@Override
	public Instruction getInstruction() throws ParserException {
		if (col.size() == 0) {
			return EmptyListLiteralInstruction.INSTANCE;
		}

		//Split Tokens where there are commas
		ArrayList<TokenQueue> listData = splitCommas(col);
		
		//Parse pops
		int pops = parsePops(listData.get(0).getArrayList());
		
		switch(listData.size()) {
		case 1:			
			ListLiteralInstruction ll = new ListLiteralInstruction(this.getSourceStringRef(), Parser.generate(listData.get(0)), pops);
			return ll;
		case 2:
		{
			StaticBlock initialList = BlockUtils.fromIS(Parser.generate(listData.get(0)));			
			InstructionStack map_is = Parser.generate(listData.get(1));
			BlockLiteralInstruction bli = map_is.getIfSingleBlockInstruction();

			StaticBlock map = null;
			if (!map_is.isEmpty()) {
				map = BlockUtils.fromIS(map_is);
			} 
			
			if (bli != null) {
				bli.setAutoEval();
			}
			
		return new ListBuilderInstruction(this.getSourceStringRef(), initialList, map, null, pops);
		}
		default:
		{ 
			StaticBlock initialList2 = BlockUtils.fromIS(Parser.generate(listData.get(0)));
			InstructionStack map2_is = Parser.generate(listData.get(1));

			StaticBlock map2 = null;
			if (!map2_is.isEmpty()) {
				map2 = BlockUtils.fromIS(map2_is);
			}
			
			StaticBlock[] filters = new StaticBlock[listData.size()-2];
			for(int k = 2; k < listData.size(); k++) {
				if (listData.get(k).size() == 0) {
					throw new SyntaxError("List Comprehension filters must not be empty [" + initialList2 + ", " + map2 + ", ]", this.getSourceStringRef());
				}
				//filters[k-2] = new BlockEvaluator(generate(listData.get(k)));
				InstructionStack tmpFilter_is = Parser.generate(listData.get(k));
				
				//If the filter clause only contains a single blockEvaluator, set them to auto eval
				BlockLiteralInstruction bli = tmpFilter_is.getIfSingleBlockInstruction();
				if (bli != null) {
					bli.setAutoEval();
				}
				
				filters[k-2] = BlockUtils.fromIS(tmpFilter_is);
			}
			return new ListBuilderInstruction(this.getSourceStringRef(), initialList2, map2, filters, pops);
		} //End default scope
		} //End switch
	}
	
	private static int parsePops(ArrayList<Token> arr) throws SyntaxError {
		int pops = 0;
		//Do we need to pop from the outer stack?
		if (arr.size() > 1 
				&& arr.get(0).isa(Token.NUMERIC)
				&& arr.get(1).isa(Token.OP)
				&& (arr.get(1).data.equals("|") || arr.get(1).data.equals(">"))) {
			NumberToken nt = (NumberToken)arr.get(0);
			try {
				pops = nt.numValue().toInt();
			} catch (NumberFormatException e) {
				throw new SyntaxError(nt + " is not a valid number in the blockEvaluator header", nt.getSourceStringRef());
			}
			
			arr.remove(0); 
			arr.remove(0);
		}
		return pops;
	}

	@Override
	public String typeString() {
		return "list";
	}
}
