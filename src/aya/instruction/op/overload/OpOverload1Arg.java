package aya.instruction.op.overload;

import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.dict.Dict;
import aya.variable.Variable;

public class OpOverload1Arg extends OpOverload {
	
	private Variable _var;
	
	public OpOverload1Arg(String name) {
		super(name);
		
		if (name.length() > 8) {
			throw new IllegalArgumentException();
		}
		
		_var =  new Variable("__" + name + "__");
	}
	
	@Override
	public boolean execute(Block block, Obj a) {
		if (a.isa(Obj.DICT)) {
			block.callVariable((Dict)a, _var);
			return true;
		} else {
			return false;
		}
	}
}
