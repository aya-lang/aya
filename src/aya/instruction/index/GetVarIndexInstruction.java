package aya.instruction.index;

import java.util.LinkedList;

import aya.Aya;
import aya.obj.Obj;
import aya.variable.Variable;

public class GetVarIndexInstruction extends GetIndexInstruction {
	
	private long _var;

	public GetVarIndexInstruction(long var) {
		_var = var;
	}
	
	@Override
	protected Obj getIndex() {
		return Aya.getInstance().getVars().getVar(_var);
	}

	@Override
	protected String repr(LinkedList<Long> visited) {
		return ".[" + Variable.decodeLong(_var) + "]";
	}
}
