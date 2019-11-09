package aya.instruction;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.LinkedList;

import aya.exceptions.AyaRuntimeException;
import aya.obj.Obj;
import aya.obj.block.Block;

public class TupleInstruction extends Instruction {	
	Block[] elements;
	
	/** 
	 * Tuples will not need to be resized during runtime
	 * @param blocks
	 */
	public TupleInstruction(Block[] blocks) {
		elements = blocks;
	}
	
	/**
	 * evals each of the blocks and returns an array containing each
	 * of the results
	 * @return
	 */
	public ArrayList<Obj> evalToResults() {
		ArrayList<Obj> out = new ArrayList<Obj>(elements.length);
		for (int i = 0; i < elements.length; i++) {
			Block b = elements[i].duplicate();
			try {
				b.eval();
			} catch (EmptyStackException e) {
				throw new AyaRuntimeException("Empty Stack in tuple");
			}
			out.add(b.pop());
		}
		return out;
	}
	
	@Override
	public void execute(Block b) {
		b.getStack().addAll(evalToResults());
	}

	@Override
	protected String repr(LinkedList<Long> visited) {
		String s = "(";
		for(int i = 0; i < elements.length; i++) {
			if(i!=0) {
				s += ", ";
			}
			s += elements[i].toString(false);
		}
		return s + ")";
	}
	
}
