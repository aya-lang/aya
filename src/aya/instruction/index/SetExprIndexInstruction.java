package aya.instruction.index;

import aya.exceptions.runtime.ValueError;
import aya.obj.Obj;
import aya.obj.block.Block;

public class SetExprIndexInstruction extends SetIndexInstruction {
	
	Block _index;
	
	public SetExprIndexInstruction(Block index) {
		_index = index;
	}
	
	protected Obj getIndex() {
		Block index = _index.duplicate();
		index.eval();
		if (index.getStack().size() == 1) {
			return index.getStack().pop();
		} else if (index.getStack().size() > 1) {
			throw new ValueError("Error attempting to index object with " + _index.repr() 
										  + ". Expression returned more than one item");
		} else {
			throw new ValueError("Error attempting to index object with " + _index.repr() 
										  + ". Expression returned nothing");
		}
		
	}
}
