package aya.instruction.variable.assignment;

import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.symbol.Symbol;
import aya.obj.symbol.SymbolConstants;
import aya.parser.SourceStringRef;

public abstract class Assignment {
	
	private SourceStringRef _source; 
	
	Assignment(SourceStringRef source) {
		_source = source;
	}
	
	public abstract void assign(Dict vars, Obj o);

	public abstract void toDict(Dict d);
	public abstract Symbol assignmentType();
	
	public SourceStringRef getSource() {
		return _source;
	}
	
	public Dict toDict() {
		Dict d = new Dict();
		d.set(SymbolConstants.ARGTYPE, assignmentType());
		toDict(d);
		return d;
	}
}
