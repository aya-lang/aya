package aya.instruction.variable.assignment;

import aya.exceptions.runtime.TypeError;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;
import aya.parser.SourceStringRef;

public class TypedAssignment extends SimpleAssignment {

	public Symbol type;

	
	public TypedAssignment(SourceStringRef source, Symbol var) {
		super(source, var);
		this.type = SymbolConstants.ANY;
	}
	
	public TypedAssignment(SourceStringRef source, Symbol var, Symbol type, boolean copy) {
		super(source, var, copy);
		if (type == null) type = SymbolConstants.ANY;
		this.type = type;
	}
	
	public void assign(Dict vars, Obj o) {
		if (Obj.isInstance(o, this.type)) {
			super.assign(vars, o);
		} else {
			TypeError e = new TypeError("Type error at argument: " + this.toString() + "\n\tExpected type: " + this.type.repr()
						+ "\n\tReceived: " + o);
			e.setSource(getSource());
			throw e;
		}
	}
	

	@Override
	public String toString() {
		String s = super.toString();
		if (this.type.id() != SymbolConstants.ANY.id()) {
			s += "::" + type.reprName();
		}
		return s;
	}

	
	@Override
	public void toDict(Dict d) {
		super.toDict(d);
		d.set(SymbolConstants.TYPE, this.type);
	}

	@Override
	public Symbol assignmentType() {
		return SymbolConstants.TYPED;
	}

}
