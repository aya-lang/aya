package aya.instruction;

import aya.ReprStream;
import aya.obj.block.Block;

public abstract class Instruction {

	public abstract void execute(Block block);
	
	public abstract ReprStream repr(ReprStream stream);
	
	@Override
	public String toString() {
		return repr(new ReprStream()).toStringOneline();
	}

}
