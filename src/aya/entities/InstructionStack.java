package aya.entities; 

import java.util.ArrayList;
import java.util.Collection;

import aya.instruction.BlockLiteralInstruction;
import aya.instruction.DataInstruction;
import aya.instruction.Instruction;
import aya.instruction.VariableSetInstruction;
import aya.instruction.flag.FlagInstruction;
import aya.instruction.variable.GetVariableInstruction;
import aya.obj.Obj;
import aya.obj.block.BlockHeader;
import aya.variable.Variable;

/**
 *  Used by the aya.Block class to hold and manage instructions
 */
public class InstructionStack {
	ArrayList<Instruction> instructions = new ArrayList<Instruction>();
	
	/** Pops the instructions from the top of the instruction stack */
	public Instruction pop() {
		return instructions.remove(instructions.size()-1);
	}
	
	/** Pushes the instruction to the top of the stack */
	public void push(Instruction o) {
		instructions.add(o);
	}
	
	/** Pushes data to the top of the instruction stack */
	public void push(Obj o) {
		push(new DataInstruction(o));
	}
	
	/** Inserts an instruction at a specified location on the stack */
	public void insert(int i, Instruction o) {
		instructions.add(i,o);
	}
	
	/** Inserts data onto the instruction stack at a specified location */
	public void insert(int i, Obj o) {
		insert(i, new DataInstruction(o));
	}
	
	/** Clears the instruction stack */
	public void clear() {
		instructions.clear();
	}
	
	/** returns at the ith element from the top of the instruction stack */
	public Instruction peek(int i) {
		return instructions.get(instructions.size()-1-i);
	}

	/** Returns the ArrayList holding all the instructions */
	public ArrayList<Instruction> getInstrucionList() {
		return instructions;
	}
	
	/** Adds a list of instructions */
	public void addAll(Collection<? extends Instruction> c) {
		instructions.addAll(c);
	}
	
	/** Adds a list of instructions */
	public void addAll(int index, Collection<? extends Instruction> c) {
		instructions.addAll(index, c);
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
		Instruction o = pop();
		int i = 0;
		int skip = 0;
		while (i < ticks) {
			if(peek(skip) instanceof FlagInstruction || peek(skip) instanceof VariableSetInstruction) {
				i--; //Ignore flags and var sets
			}
			skip++;
			i++;
		}
		instructions.add(instructions.size()-skip, o);
	}
	
	/** Return true if this instruction stack is a single block */
//	public boolean isSingleBlock() {
//		return size() == 1
//					&& peek(0) instanceof DataInstruction
//					&& (((DataInstruction)peek(0)).objIsa(Obj.BLOCK));
//	}
	
//	public boolean isSingleBlockInstruction() {
//		return size() == 1 && peek(0) instanceof BlockLiteralInstruction;
//	}
	
	/** If the instruction stack consists of a single block instruction, return it. Else return null */
	public BlockLiteralInstruction getIfSingleBlockInstruction() {
		if (size() == 1 && peek(0) instanceof BlockLiteralInstruction) {
			return (BlockLiteralInstruction)peek(0);
		} else {
			return null;
		}
	}
	
	@Override public String toString() {
		StringBuilder sb = new StringBuilder("");
		for(int i = instructions.size()-1; i >= 0; i--) {
			sb.append(instructions.get(i).toString());
			sb.append(" ");
		}
		if(sb.length() >1){
			sb.setLength(sb.length()-1);
		}
		return sb.toString();
	}

	/** Called from a block literal instruction */
	public String toStringWithCaptures(ArrayList<Variable> _captures) {
		if (instructions.size() == 0) return "";

		StringBuilder sb = new StringBuilder("");
		Instruction inst = instructions.get(instructions.size()-1);
		if (inst instanceof BlockHeader) {
			sb.append(((BlockHeader)inst).repr(_captures));
			sb.append(" ");
		}

		for(int i = instructions.size()-2; i >= 0; i--) {
			sb.append(instructions.get(i).toString());
			sb.append(" ");
		}

		if(sb.length() >1){
			sb.setLength(sb.length()-1);
		}
		return sb.toString();
	}
	
	/** Creates a deep copy of the InstructionStack */
	public InstructionStack duplicate() {
		InstructionStack is = new InstructionStack();
		is.addAll(this.instructions);
		return is;
	}

	
	/** Finds all vars with id matching varid and swaps them
	 * with `item`
	 */
	public void assignVarValue(long varid, Obj item) {
		for (int i = 0; i < instructions.size(); i++) {
			final Instruction o = instructions.get(i);
			if (o instanceof GetVariableInstruction && ((GetVariableInstruction)o).getID() == varid) {
				instructions.set(i, new DataInstruction(item));
			}
		}
	}

}
