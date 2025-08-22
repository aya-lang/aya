package aya.instruction.variable.assignment;

import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.number.Num;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;
import aya.parser.SourceStringRef;

public class CopyAssignment extends SimpleAssignment {
	
	private boolean _copy;
	
	public CopyAssignment(SourceStringRef source, Symbol var, boolean copy) {
		super(source, var);
		_copy = copy;
	}

	@Override
	public void assign(Dict vars, Obj o) {
		super.assign(vars, _copy ? o.deepcopy() : o);
	}

	@Override
	public void toDict(Dict d) {
		super.toDict(d);
		d.set(SymbolConstants.COPY, Num.fromBool(this._copy));
	}
	
	@Override
	public String toString() {
		return super.toString() + (_copy ? "$" : "");
	}

}
