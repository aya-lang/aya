package aya.instruction.index;

import aya.ReprStream;
import aya.exceptions.TypeError;
import aya.instruction.Instruction;
import aya.instruction.op.Ops;
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
		boolean keyvar = false;
		
		if (container.isa(Obj.LIST)) {
			Casting.asList(container).mutSetIndexed(index, value);
		} else if (container.isa(Obj.DICT)) {
			Dict d = Casting.asDict(container);
			if (d.hasMetaKey(Ops.KEYVAR_SETINDEX)) {
				keyvar = true;
				block.push(value);
				block.push(index);
				block.callVariable(d, Ops.KEYVAR_SETINDEX);
			} else {
				Dict.setIndex((Dict)container, index, value);
			}
		} else {
			throw new TypeError("Cannot index object:\n  " + container.repr() + repr(new ReprStream()));
		}
		
		// Add the container back to the stack
		if (!keyvar) block.push(container);
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print(".:[");
		getIndex().repr(stream);
		stream.print("]");
		return stream;
	}
}
