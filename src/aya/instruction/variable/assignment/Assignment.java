package aya.instruction.variable.assignment;

import java.util.ArrayList;

import aya.eval.ExecutionContext;
import aya.obj.Obj;
import aya.obj.dict.Dict;
import aya.obj.symbol.Symbol;
import aya.parser.SourceStringRef;

public abstract class Assignment {
	
	protected SourceStringRef _source; 
	
	Assignment(SourceStringRef source) {
		_source = source;
	}
	
	public abstract void assign(Dict vars, Obj o, ExecutionContext ctx);

	public abstract void toDict(Dict d);
	
	public SourceStringRef getSource() {
		return _source;
	}
	
	/** Must return a copy if anything changes
	 * If nothing changes, it is okay to return the same object
	 * 
	 * @param ctx
	 */
	public Assignment setTypeInfo(ExecutionContext ctx) {
		return this;
	}
	
	public boolean hasTypeInfo() {
		return false;
	}
	
	public Dict toDict() {
		Dict d = new Dict();
		toDict(d);
		return d;
	}

	public abstract ArrayList<Symbol> getNames();


}
