package aya.instruction.flag;

import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.InternalAyaRuntimeException;
import aya.obj.symbol.SymbolConstants;
import aya.parser.SourceStringRef;

public class TieFlagInstruction extends FlagInstruction {
	
	public TieFlagInstruction(SourceStringRef source) {
		super(source);
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		throw new InternalAyaRuntimeException(SymbolConstants.ERROR, "Attempted t;o execute a tie instruction. This should not be possible is caused by a bug in the parser");
	}

}
