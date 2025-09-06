package aya.obj.block;

import java.util.ArrayList;

import aya.ReprStream;
import aya.eval.ExecutionContext;
import aya.instruction.variable.assignment.TypedAssignment;
import aya.obj.dict.Dict;
import aya.obj.symbol.Symbol;
import aya.parser.SourceStringRef;
import aya.util.Pair;
import aya.util.Triple;

public class CheckReturnTypeGenerator {
	public static final CheckReturnTypeGenerator EMPTY = new CheckReturnTypeGenerator(new ArrayList<Pair<Symbol,StaticBlock>>());
	
	// StaticBlock may be null (represents any)
	private ArrayList<Pair<Symbol, StaticBlock>> _items;
	private SourceStringRef _source;
	
	public CheckReturnTypeGenerator(ArrayList<Pair<Symbol, StaticBlock>> items) {
		_items = items;
	}
	
	public CheckReturnTypeInstance makeCheckReturnTypeInstruction(ExecutionContext ctx) {
		
		var out = new ArrayList<Triple<Symbol, Dict, StaticBlock>>();
		for (var item : _items) {
			Dict type_dict = null;
			if (item.second() != null) {
				type_dict = TypedAssignment.getTypeDictFromBlock(item.second(), _source, ctx);
			}
			out.add(new Triple<Symbol, Dict, StaticBlock>(item.first(), type_dict, item.second()));
		}
		
		return new CheckReturnTypeInstance(out);
	}
	
	public ReprStream repr(ReprStream stream) {
		boolean tight = stream.isTight();
		stream.setTight(true);
		for (var item : _items) {
			stream.print(item.first().name());
			stream.print("::");
			item.second().repr(stream);
			stream.print(" ");
		}
		// return back to original state
		stream.setTight(tight);
		return stream;
	}
}
