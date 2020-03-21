package aya.instruction.named;

import java.util.LinkedList;

import aya.instruction.Instruction;

public abstract class NamedInstruction extends Instruction {
	
	private String _name;
	
	public NamedInstruction(String name) {
		this._name = name;
	}
	
	public String getName() {
		return this._name;
	}

	@Override
	protected String repr(LinkedList<Long> visited) {
		return ":{" + _name + "}";
	}

}
