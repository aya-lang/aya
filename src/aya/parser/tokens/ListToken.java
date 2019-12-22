package aya.parser.tokens;

import java.util.ArrayList;

import aya.exceptions.SyntaxError;
import aya.instruction.BlockLiteralInstruction;
import aya.instruction.EmptyListLiteralInstruction;
import aya.instruction.Instruction;
import aya.instruction.ListBuilder;
import aya.instruction.ListLiteralInstruction;
import aya.obj.block.Block;
import aya.parser.Parser;
import aya.parser.token.TokenQueue;

public class ListToken extends CollectionToken {
	
	public ListToken(String data, ArrayList<Token> col) {
		super(Token.LIST, data, col);
	}

	
	@Override
	public Instruction getInstruction() {
		if (col.size() == 0) {
			return EmptyListLiteralInstruction.INSTANCE;
		}

		//Split Tokens where there are commas
		ArrayList<TokenQueue> listData = splitCommas(col);
		
		//Parse pops
		int pops = parsePops(listData.get(0).getArrayList());
		
		switch(listData.size()) {
		case 1:			
			ListLiteralInstruction ll = new ListLiteralInstruction(Parser.generate(listData.get(0)), pops);
			return ll;
		case 2:
		{
			Block initialList = new Block(Parser.generate(listData.get(0)));			
			Block map = new Block(Parser.generate(listData.get(1)));
			BlockLiteralInstruction bli = map.getInstructions().getIfSingleBlockInstruction();

			if (map.getInstructions().isEmpty()) {
				map = null;
			} 
			
			if (bli != null) {
				if (!bli.isRawBlock()) {
					throw new SyntaxError("List comprehension literal cannot contain a block with captures:\n" + data);
				} else {
					bli.setAutoEval();
				}
			}
			
		return new ListBuilder(initialList, map, null, pops);
		}
		default:
		{ 
			Block initialList2 = new Block(Parser.generate(listData.get(0)));
			Block map2 = new Block(Parser.generate(listData.get(1)));
			if (map2.getInstructions().isEmpty()) {
				map2 = null;
			}
			
			Block[] filters = new Block[listData.size()-2];
			for(int k = 2; k < listData.size(); k++) {
				if (listData.get(k).size() == 0) {
					throw new SyntaxError("List Comprehension filters must not be empty [" + initialList2 + ", " + map2 + ", ]");
				}
				//filters[k-2] = new Block(generate(listData.get(k)));
				Block tmpFilter = new Block(Parser.generate(listData.get(k)));
				
				//If the filter clause only contains a single block, set them to auto eval
				BlockLiteralInstruction bli = tmpFilter.getInstructions().getIfSingleBlockInstruction();
				if (bli != null) {
					if (!bli.isRawBlock()) {
						throw new SyntaxError("List comprehension literal cannot contain a block with captures:\n[" + data + "]");
					} else {
						bli.setAutoEval();
					}
				}
				
				filters[k-2] = tmpFilter;
			}
			return new ListBuilder(initialList2, map2, filters, pops);
		} //End default scope
		} //End switch
	}
	
	private static int parsePops(ArrayList<Token> arr) {
		int pops = 0;
		//Do we need to pop from the outer stack?
		if (arr.size() > 1 
				&& arr.get(0).isa(Token.NUMERIC)
				&& arr.get(1).isa(Token.OP)
				&& arr.get(1).data.equals("|")) {
			NumberToken nt = (NumberToken)arr.get(0);
			try {
				pops = nt.numValue().toInt();
			} catch (NumberFormatException e) {
				throw new SyntaxError(nt + " is not a valid number in the block header");
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
