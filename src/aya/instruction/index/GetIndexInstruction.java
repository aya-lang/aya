package aya.instruction.index;

import aya.ReprStream;
import aya.exceptions.TypeError;
import aya.instruction.Instruction;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.dict.Dict;
import aya.obj.dict.DictIndexing;
import aya.util.Casting;

public abstract class GetIndexInstruction extends Instruction {
	
	protected abstract Obj getIndex();
	
	public void execute(Block block) {
		final Obj o = block.pop();
		
		Obj index = getIndex();
		
		if (o.isa(Obj.LIST)) {
			block.push(Casting.asList(o).getIndexed(index));
		} else if (o.isa(Obj.DICT)) {
			block.push(DictIndexing.getIndex((Dict)o, index));
		} else {
			throw new TypeError("Cannot index object:\n  " + o.repr() + repr(new ReprStream()));
		}
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print(".[");
		getIndex().repr(stream);
		stream.print("]");
		return stream;
	}
}
