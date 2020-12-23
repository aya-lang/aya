package aya.instruction.flag;

import aya.Aya;
import aya.obj.block.Block;

/**
 *  Pop a variable set from the variable scope stack
 */
public class PopVarFlagInstruction extends FlagInstruction {
	
	public static final PopVarFlagInstruction INSTANCE = new PopVarFlagInstruction();
	
	private PopVarFlagInstruction() { }
	
	@Override
	public void execute(Block b) {
		Aya.getInstance().getVars().pop();
	}
	
	@Override
	public String toString() {
		//return "`POPVAR`";
		return "";
	}
}
