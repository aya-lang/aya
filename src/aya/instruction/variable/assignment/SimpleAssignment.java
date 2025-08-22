package aya.instruction.variable.assignment;

import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.number.Num;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;
import aya.parser.SourceStringRef;

public class SimpleAssignment extends Assignment {
	
	private Symbol _var;
	private boolean _copy;
	
	public SimpleAssignment(SourceStringRef source, Symbol var) {
		super(source);
		_var = var;
		_copy = false;
	}
	
	public SimpleAssignment(SourceStringRef source, Symbol var, boolean copy) {
		super(source);
		_var = var;
		_copy = copy;
	}

	@Override
	public void assign(Dict vars, Obj o) {
		if (_copy) {
			vars.set(this._var, o.deepcopy());
		} else {
			vars.set(this._var, o);
		}
	}

	@Override
	public void toDict(Dict d) {
		d.set(SymbolConstants.NAME, _var);
		d.set(SymbolConstants.COPY, Num.fromBool(this._copy));
	}

	@Override
	public Symbol assignmentType() {
		return SymbolConstants.SIMPLE;
	}
	
	@Override
	public String toString() {
		return _var.name() + (_copy ? "$" : "");

	}

}
