package aya.instruction.named;

import aya.ReprStream;
import aya.instruction.Instruction;

public abstract class NamedInstruction extends Instruction {
	
	protected String _name;
	protected String _doc;
	
	public NamedInstruction(String name) {
		super(null); // TODO
		this._name = name;
		this._doc = null;
	}

	public NamedInstruction(String name, String doc) {
		super(null); // TODO
		this._name = name;
		this._doc = doc;
	}
	
	public String getName() {
		return this._name;
	}
	
	public String opName() {
		return ":{" + _name + "}";
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print(opName());
		return stream;
	}

}
