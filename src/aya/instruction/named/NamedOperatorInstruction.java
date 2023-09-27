package aya.instruction.named;

import aya.ReprStream;
import aya.instruction.Instruction;
import aya.obj.block.Block;
import aya.parser.SourceStringRef;

public class NamedOperatorInstruction extends Instruction {

	private NamedOperator op;
	
	public NamedOperatorInstruction(SourceStringRef source, NamedOperator op) {
		super(source);
		this.op = op;
	}

	@Override
	public void execute(Block block) {
		this.op.execute(block);
		
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		return this.op.repr(stream);
	}
}
