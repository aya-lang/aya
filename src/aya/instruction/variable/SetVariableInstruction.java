package aya.instruction.variable;

import java.util.LinkedList;

import aya.Aya;
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
	protected String repr(LinkedList<Long> visited) {
		return ":" + SymbolEncoder.decodeLong(variable_);
	}
}
