package aya.instruction.op.overload;

import java.util.ArrayList;

public class OpOverloadNoOp extends OpOverload {

	public OpOverloadNoOp(String name) {
		_name = name;
	}

	@Override
	public ArrayList<String> getNames() {
		ArrayList<String> out = new ArrayList<String>();
		out.add("__" + _name + "__");
		return out;
	}

}
