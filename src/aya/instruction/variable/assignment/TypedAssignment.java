package aya.instruction.variable.assignment;

import aya.exceptions.runtime.TypeError;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.number.Num;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;
import aya.parser.SourceStringRef;

public class TypedAssignment extends Assignment {

	public Symbol var;
	public Symbol type;
	public boolean copy;
	
	public TypedAssignment(SourceStringRef source, Symbol var) {
		super(source);
		this.var = var;
		this.type = SymbolConstants.ANY;
		this.copy = false;
	}
	
	public TypedAssignment(SourceStringRef source, Symbol var, Symbol type, boolean copy) {
		super(source);
		if (type == null) type = SymbolConstants.ANY;
		this.var = var;
		this.type = type;
		this.copy = copy;
	}
	
	public void assign(Dict vars, Obj o) {
		if (Obj.isInstance(o, this.type)) {
			if (this.copy) {
				vars.set(this.var, o.deepcopy());
			} else {
				vars.set(this.var, o);
			}
		} else {
			TypeError e = new TypeError("{ARGS}\n\tExpected:" + this.type.repr()
						+ "\n\tReceived:" + o);
			e.setSource(getSource());
			throw e;
		}
	}
	

	@Override
	public String toString() {
		String s = var.name() + (copy ? "$" : "");
		if (this.type.id() != SymbolConstants.ANY.id()) {
			s += "::" + type.name();
		}
		return s;
	}

	
	@Override
	public void toDict(Dict d) {
		d.set(SymbolConstants.NAME, this.var);
		d.set(SymbolConstants.COPY, Num.fromBool(this.copy));
		d.set(SymbolConstants.TYPE, this.type);
	}

	@Override
	public Symbol assignmentType() {
		return SymbolConstants.TYPED;
	}

}
