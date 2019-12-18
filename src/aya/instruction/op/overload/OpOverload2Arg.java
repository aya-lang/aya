package aya.instruction.op.overload;

import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.dict.Dict;
import aya.variable.Variable;

public class OpOverload2Arg extends OpOverload {
	
	private Variable _var;
	private Variable _rvar;

	public OpOverload2Arg(String name) {
		super(name);

		if (name.length() > 7) {
			throw new IllegalArgumentException();
		}
		
		_var  =  new Variable("__"  + name + "__");
		_rvar =  new Variable("__r" + name + "__");
	}
	
	@Override
	public boolean execute(Block block, Obj a, Obj b) {
		if (a.isa(Obj.DICT)) {
			block.push(b);
			block.callVariable((Dict)a, _var);
			return true;
		} else if (b.isa(Obj.DICT)) {
			block.callVariable((Dict)b, _rvar, a);
			return true;
		} else {
			return false;
		}
	}

}
