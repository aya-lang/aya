package aya.instruction.named;

import aya.ReprStream;
import aya.obj.block.BlockEvaluator;

public abstract class NamedOperator {
	
	protected String _name;
	protected String _doc;
	
	public NamedOperator(String name) {
		this._name = name;
		this._doc = null;
	}

	public NamedOperator(String name, String doc) {
		this._name = name;
		this._doc = doc;
	}

	public abstract void execute(BlockEvaluator blockEvaluator);
	
	public String getName() {
		return this._name;
	}
	
	public String opName() {
		return ":{" + _name + "}";
	}

	public ReprStream repr(ReprStream stream) {
		stream.print(opName());
		return stream;
	}

}
