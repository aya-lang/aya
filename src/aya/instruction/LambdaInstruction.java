package aya.instruction;

import aya.ReprStream;
import aya.obj.block.Block;

/**
 * The Lambda class is nothing more than a block that automatically
 * gets dumped at runtime.
 * 
 * @author npaul
 *
 */
public class LambdaInstruction extends Instruction {
	InstructionStack instructions;
	
	public LambdaInstruction(InstructionStack instructions) {
		this.instructions = instructions;
	}
	
	/** Return the block's InstructionStack */
	public InstructionStack getInstructions() {
		return this.instructions;
	}

	@Override
	public void execute(Block b) {
		b.getInstructions().addAll(instructions.getInstrucionList());
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print("(");
		instructions.repr(stream);
		stream.print(")");
		return stream;
	}
}
