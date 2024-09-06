package aya.instruction.flag;

import aya.eval.BlockEvaluator;

/**
 *  Pop a variable set from the variable scope stack
 */
public class PopCallstackInstruction extends FlagInstruction {
	
	public static final PopCallstackInstruction INSTANCE = new PopCallstackInstruction();
	
	private PopCallstackInstruction() { }
	
	@Override
	public void execute(BlockEvaluator b) {
		b.getContext().getCallStack().pop();
	}
	
	@Override
	public String toString() {
		//return "`POPVAR`";
		return "";
	}
}
