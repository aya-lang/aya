package aya.instruction.index;

import java.util.LinkedList;

import aya.entities.operations.Ops;
import aya.exceptions.TypeError;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.dict.Dict;
import aya.obj.dict.DictIndexing;
import aya.obj.list.List;
import aya.obj.list.ListIndexing;

public class AnonGetIndexInstruction extends GetIndexInstruction {
	
	@Override
	public void execute (final Block block) {
		Obj index = block.pop();
		final Obj list = block.pop();
				
		if(list.isa(Obj.LIST)) {		
			block.push(ListIndexing.getIndex((List)list, index));
		} else if (list.isa(Obj.DICT)) {
			block.push(DictIndexing.getIndex((Dict)list, index));
		} else {
			throw new TypeError(Ops.getOp('I'), index, list);
		}
	}

	@Override
	protected String repr(LinkedList<Long> visited) {
		return "<AnonGetIndexInstruction>";
	}

	@Override
	protected Obj getIndex() {
		throw new RuntimeException("AnonGetIndexInstruction.getIndex() is unimplemented");
	}
}
