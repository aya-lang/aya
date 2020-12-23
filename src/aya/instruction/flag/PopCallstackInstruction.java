package aya.instruction.flag;

import aya.Aya;
import aya.obj.block.Block;

/**
 *  Pop a variable set from the variable scope stack
 */
public class PopCallstackInstruction extends FlagInstruction {
	
	public static final PopCallstackInstruction INSTANCE = new PopCallstackInstruction();
	
	private PopCallstackInstruction() { }
	
	@Override
	public void execute(Block b) {
		Aya.getInstance().getCallStack().pop();
	}
	
	@Override
	public String toString() {
		//return "`POPVAR`";
		return "";
	}
}
