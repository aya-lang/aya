package aya.entities;

import java.util.Stack;

import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.list.GenericList;
import aya.obj.list.List;

import java.util.ArrayList;

public class ListLiteral extends Block {
	//The number of items that should be popped from the stack and added to the front of this list
	int pops;
	
	public ListLiteral(InstructionStack is, int num_pops) {
		super(is);
		this.pops = num_pops;
	}
	
	public int getPops() {
		return pops;
	}
	
	public List getListCopy(Stack<Obj> outerStack) {
		ListLiteral ll = this.duplicate();
		int p = pops;
		
		//Remove the pops from the outer stack
		while (p > 0) {
			p--;
			ll.instructions.push(outerStack.pop());
		}
		
		ll.eval();
		return new GenericList(new ArrayList<Obj>(ll.stack)).promote();
	}
	
	@Override
	public ListLiteral duplicate() {
		ListLiteral ll = new ListLiteral(this.instructions.duplicate(), this.pops);
		ll.stack.addAll(this.stack);
		return ll;
	}
	
	@Override
	public String toString() {
		return "[" + instructions + "]";
	}
}
