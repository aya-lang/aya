package aya.instruction.named;

import aya.ReprStream;
import aya.eval.BlockEvaluator;
import aya.instruction.Instruction;
import aya.parser.SourceStringRef;

public class NamedOperatorInstruction extends Instruction {

	private NamedOperator op;
	
	public NamedOperatorInstruction(SourceStringRef source, NamedOperator op) {
		super(source);
		this.op = op;
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		this.op.execute(blockEvaluator);
		
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		return this.op.repr(stream);
	}
}
