package aya.instruction.index;

import aya.exceptions.runtime.ValueError;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.block.StaticBlock;
import aya.parser.SourceStringRef;

public class SetExprIndexInstruction extends SetIndexInstruction {
	
	StaticBlock _index;
	
	public SetExprIndexInstruction(SourceStringRef source, StaticBlock index) {
		super(source);
		_index = index;
	}
	
	protected Obj getIndex() {
		Block index = new Block(_index);
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
