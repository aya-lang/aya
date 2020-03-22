package aya.instruction.named;

import java.util.LinkedList;

import aya.instruction.Instruction;

public abstract class NamedInstruction extends Instruction {
	
	private String _name;
	protected String _doc;
	
	public NamedInstruction(String name) {
		this._name = name;
		this._doc = null;
	}
	
	public String getName() {
		return this._name;
	}
	
	public String opName() {
		return ":{" + _name + "}";
	}

	@Override
	protected String repr(LinkedList<Long> visited) {
		return opName();
	}

}
