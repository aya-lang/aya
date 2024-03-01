package aya.instruction.index;

import aya.ReprStream;
import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.TypeError;
import aya.instruction.op.Ops;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.dict.DictIndexing;
import aya.util.Casting;

public class AnonGetIndexInstruction extends GetIndexInstruction {
	
	public AnonGetIndexInstruction() {
		super(null);
	}
	
	@Override
	public void execute (final BlockEvaluator blockEvaluator) {
		Obj index = blockEvaluator.pop();
		final Obj list = blockEvaluator.pop();
				
		if(list.isa(Obj.LIST)) {		
			blockEvaluator.push(Casting.asList(list).getIndexed(index));
		} else if (list.isa(Obj.DICT)) {
			blockEvaluator.push(DictIndexing.getIndex((Dict)list, index));
		} else {
			throw new TypeError(Ops.OP_I_INSTANCE, index, list);
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
