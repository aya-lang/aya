package aya.instruction.index;

import java.util.LinkedList;

import aya.exceptions.TypeError;
import aya.instruction.Instruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.dict.Dict;
import aya.obj.list.List;
import aya.obj.list.ListIndexing;

public abstract class SetIndexInstruction extends Instruction {
	
	protected abstract Obj getIndex();
	
	public void execute(Block block) {
		final Obj container = block.pop();
		final Obj value = block.pop();
		
		Obj index = getIndex();
		
		if (container.isa(Obj.LIST)) {
			ListIndexing.setIndex((List)container, index, value);
		} else if (container.isa(Obj.DICT)) {
			Dict.setIndex((Dict)container, index, value);
		} else {
			throw new TypeError("Cannot index object:\n  " + container.repr() + repr(new LinkedList<Long>()));
		}
		
		// Add the container back to the stack
		block.push(container);
	}

	@Override
	protected String repr(LinkedList<Long> visited) {
		return ".[" + getIndex().repr() + "]";
	}
}
