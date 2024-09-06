package aya.instruction.index;

import aya.exceptions.runtime.TypeError;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.dict.Dict;
import aya.obj.dict.DictIndexing;
import aya.obj.number.Num;
import aya.parser.SourceStringRef;
import aya.util.Casting;

public class GetNumberIndexInstruction extends GetIndexInstruction {
	
	private int _index;
	
	public GetNumberIndexInstruction(SourceStringRef source, int index) {
		super(source);
		_index = index;
	}
	
	@Override
	protected Obj getIndex() {
		return Num.fromInt(_index);
	}

	@Override
	public void execute(Block block) {
		final Obj o = block.pop();
		try {
			// Most likely a list, attempt to index it
			block.push(Casting.asList(o).getIndexed(_index));
		} catch (ClassCastException e) {
			// Not a list, is it a dict?
			if (o.isa(Obj.DICT)) {
				block.push(DictIndexing.getIndex((Dict)o, Num.fromInt(_index)));
			} else {
				throw new TypeError("Unable to access object at numeric index [" + _index + "]:\n" + o.repr() );
			}
		}
	}
}
