package aya.instruction.variable;

import java.util.LinkedList;

import aya.Aya;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.variable.Variable;

public class GetVariableInstruction extends VariableInstruction {

	public GetVariableInstruction(long id) {
		this.variable_ = id;
	}
	
	@Override
	public void execute(Block b) {
		Obj o = Aya.getInstance().getVars().getVar(variable_);
		addOrDumpVar(o, b);
	}
	
	/**
	 * If o is a block, dump it's instructions. Otherwise, add it to the stack
	 * @param o
	 * @param b
	 */
	public static void addOrDumpVar(Obj o, Block b) {
		if (o.isa(Obj.BLOCK)) {
			b.getInstructions().addAll(((Block)o).getInstructions().getInstrucionList());
		} else {
			b.push(o);
		}
	}
	
	@Override
	protected String repr(LinkedList<Long> visited) {
		return Variable.decodeLong(variable_);
	}
}
