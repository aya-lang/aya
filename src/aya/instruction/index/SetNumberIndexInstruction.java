package aya.instruction.index;

import aya.obj.Obj;
import aya.obj.number.Num;
import aya.parser.SourceStringRef;

public class SetNumberIndexInstruction extends SetIndexInstruction {
	
	private int _index;
	
	public SetNumberIndexInstruction(SourceStringRef source, int index) {
		super(source);
		_index = index;
	}
	
	@Override
	protected Obj getIndex() {
		return Num.fromInt(_index);
	}
}
