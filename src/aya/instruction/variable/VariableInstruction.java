package aya.instruction.variable;

import aya.instruction.Instruction;
import aya.obj.symbol.Symbol;

public abstract class VariableInstruction extends Instruction {

	protected Symbol variable_;
	
	protected VariableInstruction(Symbol var) {
		variable_ = var;
	}
	
	public String varName() {
		return variable_.name();
	}
	
	public Symbol getSymbol() {
		return variable_;
	}

}
