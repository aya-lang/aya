package aya.instruction.variable;

import aya.instruction.Instruction;
import aya.obj.symbol.SymbolEncoder;

public abstract class VariableInstruction extends Instruction {

	protected long variable_;
	
	public String varName() {
		return SymbolEncoder.decodeLong(variable_);
	}
	
	public long id() {
		return variable_;
	}

}
