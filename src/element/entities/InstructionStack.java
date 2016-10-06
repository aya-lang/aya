package element.entities; 

import java.util.ArrayList;
import java.util.Collection;

import element.ElemTypes;

/**
 *  Used by the element.Block class to hold and manage instructions
 */
public class InstructionStack {
	ArrayList<Object> instructions = new ArrayList<Object>();
	
	/** Pops the instructions from the top of the instruction stack */
	public Object pop() {
		return instructions.remove(instructions.size()-1);
	}
	
	/** Pushes the instruction to the top of the stack */
	public void push(Object o) {
		instructions.add(o);
	}
	
	/** Inserts an instruction at a specified location on the stack */
	public void insert(int i, Object o) {
		instructions.add(i,o);
	}
	
	/** Clears the instruction stack */
	public void clear() {
		instructions.clear();
	}
	
	/** returns at the ith element from the top of the instruction stack */
	public Object peek(int i) {
		return instructions.get(instructions.size()-1-i);
	}

	/** Returns the ArrayList holding all the instructions */
	public ArrayList<Object> getInstrucionList() {
		return instructions;
	}
	
	/** Adds a list of instructions */
	public void addAll(Collection<? extends Object> c) {
		instructions.addAll(c);
	}
	
	/** Returns true if there are no more instructions */
	public boolean isEmpty() {
		return instructions.isEmpty();
	}
	
	/** Returns the number of instructions in the instruction stack */
	public int size() {
		return instructions.size();
	}
	
	/** Will move the top of the instruction list back into the instruction list i times */
	public void holdNext(int ticks) {
		Object o = pop();
		int i = 0;
		int skip = 0;
		while (i < ticks) {
			if(ElemTypes.isFlag(peek(skip)) || ElemTypes.isVarSet(peek(skip))) {
				i--; //Ignore flags and var sets
			}
			skip++;
			i++;
		}
		instructions.add(instructions.size()-skip, o);
	}
	
	@Override public String toString() {
		StringBuilder sb = new StringBuilder("");
		for(int i = instructions.size()-1; i >= 0; i--) {
			sb.append(ElemTypes.show(instructions.get(i)));
			sb.append(" ");
		}
		if(sb.length() >1){
			sb.setLength(sb.length()-1);
		}
		return sb.toString();
	}
	
	/** Creates a dep copy of the InstructionStack */
	public InstructionStack duplicate() {
		InstructionStack is = new InstructionStack();
		is.addAll(this.instructions);
		return is;
	}

	/** Adds the object to the stack. If the object is
	 * an InstructionStack, add all of its items */
	public void addISorOBJ(Object o) {
		if(o instanceof InstructionStack) {
			addAll(((InstructionStack)o).getInstrucionList());
		} else {
			push(o);
		}
	}
}
