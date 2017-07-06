package aya.entities;

import java.util.ArrayList;
import java.util.Stack;

import aya.obj.Obj;
import aya.obj.block.Block;
import aya.obj.list.GenericList;
import aya.obj.list.List;

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
	
	/** Returns null if is not list literal */
	public List toList() {
		ArrayList<Obj> items = new ArrayList<Obj>();
		ArrayList<Object> instrs = instructions.getInstrucionList();
		for (int i = instrs.size()-1; i>=0; i--) {
			if (instrs.get(i) instanceof Obj) {
				items.add((Obj)instrs.get(i));
			} else {
				return null;
			}
		}
		return new GenericList(items).promote();
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
