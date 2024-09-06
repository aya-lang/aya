package aya.instruction.index;

import aya.obj.Obj;
import aya.parser.SourceStringRef;

public class SetObjIndexInstruction extends SetIndexInstruction {
	
	Obj _index;
	
	public SetObjIndexInstruction(SourceStringRef source, Obj index) {
		super(source);
		_index = index;
	}
	
	protected Obj getIndex() {
		return _index;
	}
}
