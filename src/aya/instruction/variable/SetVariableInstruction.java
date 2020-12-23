package aya.instruction.variable;

import aya.Aya;
import aya.ReprStream;
import aya.obj.block.Block;
import aya.obj.symbol.SymbolEncoder;

public class SetVariableInstruction extends VariableInstruction {
	
	public SetVariableInstruction(long id) {
		this.variable_ = id;
	}
	
	@Override
	public void execute(Block b) {
		Aya.getInstance().getVars().setVar(variable_, b.peek());
	}
	
	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print(":" + SymbolEncoder.decodeLong(variable_));
		return stream;
	}
}
