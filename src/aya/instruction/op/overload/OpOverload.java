package aya.instruction.op.overload;

import aya.obj.Obj;
import aya.obj.block.Block;

public class OpOverload {
	
	private String _name;
	
	public OpOverload(String name) {
		_name = name;
	}

	public boolean execute(Block block, Obj a, Obj b) {
		throw new UnsupportedOperationException("Op overload '" + _name + "' does not support using two arguments");
	}

	public boolean execute(Block block, Obj a) {
		throw new UnsupportedOperationException("Op overload '" + _name + "' does not support using one argument");
	}
}
