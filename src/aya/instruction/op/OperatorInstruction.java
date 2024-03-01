package aya.instruction.op;

import aya.ReprStream;
import aya.eval.BlockEvaluator;
import aya.instruction.Instruction;
import aya.parser.SourceStringRef;

public class OperatorInstruction extends Instruction {

	private Operator op;
	
	public OperatorInstruction(SourceStringRef source, Operator op) {
		super(source);
		if (op == null) throw new AssertionError();
		this.op = op;
	}
	
	public Operator getOperator() {
		return this.op;
	}
	
	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		op.execute(blockEvaluator);
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		return op.repr(stream);
	}

}
