package aya.instruction.op;

import aya.ReprStream;
import aya.instruction.Instruction;
import aya.obj.block.Block;
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
	public void execute(Block block) {
		op.execute(block);
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		return op.repr(stream);
	}

}
