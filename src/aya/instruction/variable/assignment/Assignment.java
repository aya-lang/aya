package aya.instruction.variable.assignment;

import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.parser.SourceStringRef;

public abstract class Assignment {
	
	private SourceStringRef _source; 
	
	Assignment(SourceStringRef source) {
		_source = source;
	}
	
	public abstract void assign(Dict vars, Obj o);

	public abstract void toDict(Dict d);
	
	public SourceStringRef getSource() {
		return _source;
	}
	
	public Dict toDict() {
		Dict d = new Dict();
		toDict(d);
		return d;
	}
}
