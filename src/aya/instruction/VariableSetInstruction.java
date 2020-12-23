package aya.instruction;

import aya.Aya;
import aya.ReprStream;
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
		Aya.getInstance().getVars().add(vars);
	}
	
	public VariableSet getVars() {
		return _vars;
	}
	
	public ReprStream repr(ReprStream stream) {
		_vars.reprHeader(stream);
		return stream;
	}
}
