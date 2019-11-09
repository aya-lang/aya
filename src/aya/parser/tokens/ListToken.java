package aya.parser.tokens;

import java.util.ArrayList;

import aya.entities.ListBuilder;
import aya.exceptions.SyntaxError;
import aya.instruction.EmptyListLiteralInstruction;
import aya.instruction.ListLiteralInstruction;
import aya.obj.block.Block;
import aya.obj.number.Number;
import aya.parser.Parser;
import aya.parser.token.TokenQueue;

public class ListToken extends CollectionToken {
	
	public ListToken(String data, ArrayList<Token> col) {
		super(Token.LIST, data, col);
	}

	
	@Override
	public Object getAyaObj() {
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
			if (map.getInstructions().isEmpty()) {
				map = null;
			} 
			
			//If the map clause only contains a single block (and it is not a list literal), dump its instructions into the map block
			else if (map.getInstructions().size() == 1 
					&& map.getInstructions().peek(0) instanceof Block ){
					//&& !ElemTypes.toBlock(map.getInstructions().peek(0)).isListLiteral()) {
				map = new Block(( (Block)(map.getInstructions().peek(0)) ).getInstructions() );
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
				
				//If the filter clause only contains a single block, dump its instructions into the filter block
				if (tmpFilter.getInstructions().size() == 1 && tmpFilter.getInstructions().peek(0) instanceof Block) {
					tmpFilter = new Block(((Block)(tmpFilter.getInstructions().peek(0))).getInstructions());
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
			pops = ((Number)arr.get(0).getAyaObj()).toInt();
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
