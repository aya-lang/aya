package aya.instruction; 

import java.util.ArrayList;
import java.util.Collection;

import aya.ReprStream;
import aya.exceptions.runtime.ValueError;
import aya.instruction.flag.FlagInstruction;
import aya.instruction.variable.GetVariableInstruction;
import aya.obj.Obj;
import aya.obj.block.BlockHeader;
import aya.obj.symbol.Symbol;

/**
 *  Used by the aya.Block class to hold and manage instructions
 */
public class InstructionStack {
	ArrayList<Instruction> instructions = new ArrayList<Instruction>();
	
	/** Pops the instructions from the top of the instruction stack */
	public Instruction pop() {
		return instructions.remove(instructions.size()-1);
	}

	/** Pops the instructions from the top of the instruction stack */
	public Instruction pop(int i) {
		return instructions.remove(instructions.size()-1-i);
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
			if(peek(skip) instanceof FlagInstruction) {
				i--; //Ignore flags
			}
			skip++;
			i++;
		}
		instructions.add(instructions.size()-skip, o);
	}
	
	public Instruction popNextNonFlagInstruction() throws ValueError {
		for (int i = 0; i < instructions.size(); i++) {
			if (!(peek(i) instanceof FlagInstruction)) {
				return pop(i);
			}
		}
		
		throw new ValueError("Empty instruction stack");
	}
	
	
	/** If the instruction stack consists of a single block instruction, return it. Else return null */
	public BlockLiteralInstruction getIfSingleBlockInstruction() {
		if (size() == 1 && peek(0) instanceof BlockLiteralInstruction) {
			return (BlockLiteralInstruction)peek(0);
		} else {
			return null;
		}
	}
	

	/** Creates a deep copy of the InstructionStack */
	public InstructionStack duplicate() {
		InstructionStack is = new InstructionStack();
		is.addAll(this.instructions);
		return is;
	}
	
	/** Set the header of the instructions */
	public void replaceHeader(BlockHeader bh) {
		int i = instructions.size() - 1;
		if (instructions.get(i) instanceof BlockHeader) {
			instructions.set(i, bh);
		} else {
			throw new RuntimeException("Cannot replace block header, block does not already contain one");
		}
	}

	
	/** Finds all vars with id matching varid and swaps them
	 * with `item`
	 */
	public void assignVarValue(Symbol var, Obj item) {
		for (int i = 0; i < instructions.size(); i++) {
			final Instruction o = instructions.get(i);
			if (o instanceof GetVariableInstruction && ((GetVariableInstruction)o).getSymbol().id() == var.id()) {
				instructions.set(i, new DataInstruction(item));
			}
		}
	}
	

	///////////////////////
	// String Conversion //
	///////////////////////
	
	@Override
	public String toString() {
		return repr(new ReprStream()).toStringOneline();
	}

	public ReprStream repr(ReprStream stream) {
		return repr(stream, null);
	}
	
	public ReprStream repr(ReprStream stream, ArrayList<Symbol> captures) {
		if (captures != null) {
			reprWithCaptures(stream, captures);
		} else {
			for(int i = instructions.size()-1; i >= 0; i--) {
				instructions.get(i).repr(stream);
				stream.print(" ");
			}
		}
		stream.delTrailingSpaces();
		return stream;
	}

	/** Called from a block literal instruction */
	private ReprStream reprWithCaptures(ReprStream stream, ArrayList<Symbol> captures) {
		if (instructions.size() == 0) return stream;

		Instruction inst = instructions.get(instructions.size()-1);
		if (inst instanceof BlockHeader) {
			((BlockHeader)inst).repr(stream, captures);
			stream.print(" ");
		}

		for(int i = instructions.size()-2; i >= 0; i--) {
			instructions.get(i).repr(stream);
			stream.print(" ");
		}

		// Remove trailing space
		stream.backspace(1);
		return stream;
	}

}
