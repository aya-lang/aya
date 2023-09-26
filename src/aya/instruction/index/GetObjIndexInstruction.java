package aya.instruction.index;

import aya.obj.Obj;
import aya.parser.SourceStringRef;

public class GetObjIndexInstruction extends GetIndexInstruction {
	
	private Obj _index;

	public GetObjIndexInstruction(SourceStringRef source, Obj index) {
		super(source);
		_index = index;
	}
	
	@Override
	protected Obj getIndex() {
		return _index;
	}
}
