package aya.instruction.index;

import aya.ReprStream;
import aya.eval.ExecutionContext;
import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.TypeError;
import aya.instruction.Instruction;
import aya.obj.Obj;
import aya.obj.dict.DictIndexing;
import aya.parser.SourceStringRef;
import aya.util.Casting;

public abstract class GetIndexInstruction extends Instruction {
	
	public GetIndexInstruction(SourceStringRef source) {
		super(source);
	}

	protected abstract Obj getIndex();
	
	protected Obj getEvaluatedIndex(ExecutionContext context) {
		return getIndex();
	}
	
	public void execute(BlockEvaluator blockEvaluator) {
		final Obj o = blockEvaluator.pop();
		
		Obj index = getEvaluatedIndex(blockEvaluator.getContext());
		
		if (o.isa(Obj.LIST)) {
			blockEvaluator.push(Casting.asList(o).getIndexed(blockEvaluator.getContext(), index));
		} else if (o.isa(Obj.DICT)) {
			blockEvaluator.push(DictIndexing.getIndex(blockEvaluator.getContext(), Casting.asDict(o), index));
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
