package aya.instruction.variable;

import aya.instruction.Instruction;
import aya.obj.symbol.Symbol;
import aya.parser.SourceStringRef;

public abstract class VariableInstruction extends Instruction {

	protected Symbol variable_;
	
	protected VariableInstruction(SourceStringRef source, Symbol var) {
		super(source);
		variable_ = var;
	}
	
	public String varName() {
		return variable_.name();
	}
	
	public Symbol getSymbol() {
		return variable_;
	}

}
