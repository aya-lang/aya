package aya.instruction.index;

import java.util.LinkedList;

import aya.exceptions.TypeError;
import aya.instruction.Instruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.list.ListIndexing;

public abstract class GetIndexInstruction extends Instruction {
	
	protected abstract Obj getIndex();
	
	public void execute(Block block) {
		final Obj o = block.pop();
		
		Obj index = getIndex();
		
		if (o.isa(Obj.LIST)) {
			block.push(ListIndexing.getIndex((List)o, index));
		} else if (o.isa(Obj.DICT)) {
			block.push(Dict.getIndex((Dict)o, index));
		} else {
			throw new TypeError("Cannot index object:\n  " + o.repr() + repr(new LinkedList<Long>()));
		}
	}

	@Override
	protected String repr(LinkedList<Long> visited) {
		return ".[" + getIndex().repr() + "]";
	}
}
