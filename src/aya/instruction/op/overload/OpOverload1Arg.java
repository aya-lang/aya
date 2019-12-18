package aya.instruction.op.overload;

import java.util.ArrayList;

import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.dict.Dict;
import aya.variable.Variable;

public class OpOverload1Arg extends OpOverload {
	
	private Variable _var;
	
	public OpOverload1Arg(String name) {
		_name = name;
		
		if (name.length() > 8) {
			throw new IllegalArgumentException();
		}
		
		_var =  new Variable("__" + name + "__");
	}
	
	public ArrayList<String> getNames() {
		ArrayList<String> out = new ArrayList<String>();
		out.add(_var.toString());
		return out;
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
