package aya.instruction;

import java.util.LinkedList;

import aya.Aya;
import aya.obj.block.Block;
import aya.variable.VariableSet;

/**
 * Push a new scope onto the variable stack.
 * If this variable set has args, grab them off the stack
 * @author npaul
 */

public class VariableSetInstruction extends Instruction {
	
	private VariableSet _vars;
	
	public VariableSetInstruction(VariableSet vars) {
		this._vars = vars;
	}

	public void execute(Block b) {
		VariableSet vars = _vars.clone();
		vars.setArgs(b);
		vars.copyExplicitLocals();
		Aya.getInstance().getVars().add(vars);
	}
	
	public VariableSet getVars() {
		return _vars;
	}
	
	public String repr(LinkedList<Long> visited) {
		return _vars.show();
	}
}
