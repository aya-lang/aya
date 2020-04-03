package aya.instruction.index;

import aya.obj.Obj;

public class SetObjIndexInstruction extends SetIndexInstruction {
	
	Obj _index;
	
	public SetObjIndexInstruction(Obj index) {
		_index = index;
	}
	
	protected Obj getIndex() {
		return _index;
	}
}
