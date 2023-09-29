package aya.instruction.variable.assignment;

import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;

public class SimpleAssignment extends Assignment {
	
	private Symbol _var;
	
	public SimpleAssignment(Symbol var) {
		_var = var;
	}

	@Override
	public void assign(Dict vars, Obj o) {
		vars.set(_var, o);
	}

	@Override
	public void toDict(Dict d) {
		d.set(SymbolConstants.NAME, _var);
	}

	@Override
	public Symbol assignmentType() {
		return SymbolConstants.SIMPLE;
	}
	
	@Override
	public String toString() {
		return _var.name();
	}

}
