package aya.instruction.flag;

import java.util.LinkedList;

import aya.Aya;
import aya.instruction.Instruction;
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
	
	@Override
	public String repr(LinkedList<Long> visited) {
		return "";
	}
}
