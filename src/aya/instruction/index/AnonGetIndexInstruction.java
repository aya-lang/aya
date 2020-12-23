package aya.instruction.index;

import aya.ReprStream;
import aya.exceptions.TypeError;
import aya.instruction.op.Ops;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.dict.Dict;
import aya.obj.dict.DictIndexing;
import aya.util.Casting;

public class AnonGetIndexInstruction extends GetIndexInstruction {
	
	@Override
	public void execute (final Block block) {
		Obj index = block.pop();
		final Obj list = block.pop();
				
		if(list.isa(Obj.LIST)) {		
			block.push(Casting.asList(list).getIndexed(index));
		} else if (list.isa(Obj.DICT)) {
			block.push(DictIndexing.getIndex((Dict)list, index));
		} else {
			throw new TypeError(Ops.getOp('I'), index, list);
		}
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print("<AnonGetIndexInstruction>");
		return stream;
	}

	@Override
	protected Obj getIndex() {
		throw new RuntimeException("AnonGetIndexInstruction.getIndex() is unimplemented");
	}
}
