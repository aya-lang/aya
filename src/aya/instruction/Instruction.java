package aya.instruction;

import aya.ReprStream;
import aya.eval.BlockEvaluator;
import aya.parser.SourceStringRef;

public abstract class Instruction {
	
	private SourceStringRef source;

	public Instruction(SourceStringRef source) {
		this.source = source;
	}

	public abstract void execute(BlockEvaluator blockEvaluator);
	
	public abstract ReprStream repr(ReprStream stream);
	
	public SourceStringRef getSource() {
		return this.source;
	}
	
	@Override
	public String toString() {
		return repr(new ReprStream()).toStringOneline();
	}

}
