package aya.instruction.index;

import aya.obj.Obj;
import aya.obj.number.Num;

public class SetNumberIndexInstruction extends SetIndexInstruction {
	
	private int _index;
	
	public SetNumberIndexInstruction(int index) {
		_index = index;
	}
	
	@Override
	protected Obj getIndex() {
		return Num.fromInt(_index);
	}
}
