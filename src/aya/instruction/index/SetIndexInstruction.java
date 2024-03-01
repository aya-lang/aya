package aya.instruction.index;

import aya.ReprStream;
import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.TypeError;
import aya.instruction.Instruction;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.symbol.SymbolConstants;
import aya.parser.SourceStringRef;
import aya.util.Casting;

public abstract class SetIndexInstruction extends Instruction {
	
	public SetIndexInstruction(SourceStringRef source) {
		super(source);
	}
	
	protected abstract Obj getIndex();
	
	public void execute(BlockEvaluator blockEvaluator) {
		final Obj container = blockEvaluator.pop();
		final Obj value = blockEvaluator.pop();
		
		Obj index = getIndex();
		boolean keyvar = false;
		
		if (container.isa(Obj.LIST)) {
			Casting.asList(container).mutSetIndexed(index, value);
		} else if (container.isa(Obj.DICT)) {
			Dict d = Casting.asDict(container);
			if (d.hasMetaKey(SymbolConstants.KEYVAR_SETINDEX)) {
				keyvar = true;
				blockEvaluator.push(value);
				blockEvaluator.push(index);
				blockEvaluator.callVariable(d, SymbolConstants.KEYVAR_SETINDEX);
			} else {
				Dict.setIndex((Dict)container, index, value);
			}
		} else {
			throw new TypeError("Cannot index object:\n  " + container.repr() + repr(new ReprStream()));
		}
		
		// Add the container back to the stack
		if (!keyvar) blockEvaluator.push(container);
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print(".:[");
		getIndex().repr(stream);
		stream.print("]");
		return stream;
	}
}
