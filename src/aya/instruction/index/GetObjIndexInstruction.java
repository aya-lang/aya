package aya.instruction.index;

import aya.obj.Obj;

public class GetObjIndexInstruction extends GetIndexInstruction {
	
	private Obj _index;

	public GetObjIndexInstruction(Obj index) {
		_index = index;
	}
	
	@Override
	protected Obj getIndex() {
		return _index;
	}
}
