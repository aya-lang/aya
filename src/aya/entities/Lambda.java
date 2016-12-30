package aya.entities;

/**
 * The Lambda class is nothing more than a block that automatically
 * gets dumped at runtime.
 * 
 * @author npaul
 *
 */
public class Lambda {
	InstructionStack list;
	
	public Lambda(InstructionStack list) {
		this.list = list;
	}
	
	/** Return the block's InstructionStack */
	public InstructionStack getInstructions() {
		return this.list;
	}
	
	@Override
	public String toString() {
		return "("+list.toString()+")";
	}
}
