package aya.instruction.variable;

import aya.instruction.Instruction;
import aya.variable.Variable;

public abstract class VariableInstruction extends Instruction {

	protected long variable_;
	
	public String varName() {
		return Variable.decodeLong(variable_);
	}
	
	public long getID() {
		return variable_;
	}

}
