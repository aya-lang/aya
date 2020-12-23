package aya.instruction.index;

import aya.ReprStream;
import aya.exceptions.TypeError;
import aya.instruction.Instruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.dict.Dict;
import aya.util.Casting;

public abstract class SetIndexInstruction extends Instruction {
	
	protected abstract Obj getIndex();
	
	public void execute(Block block) {
		final Obj container = block.pop();
		final Obj value = block.pop();
		
		Obj index = getIndex();
		
		if (container.isa(Obj.LIST)) {
			Casting.asList(container).mutSetIndexed(index, value);
		} else if (container.isa(Obj.DICT)) {
			Dict.setIndex((Dict)container, index, value);
		} else {
			throw new TypeError("Cannot index object:\n  " + container.repr() + repr(new ReprStream()));
		}
		
		// Add the container back to the stack
		block.push(container);
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print(".:[");
		getIndex().repr(stream);
		stream.print("]");
		return stream;
	}
}
