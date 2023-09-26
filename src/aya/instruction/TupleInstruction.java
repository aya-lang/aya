package aya.instruction;

import java.util.ArrayList;
import java.util.EmptyStackException;

import aya.ReprStream;
import aya.exceptions.runtime.EmptyStackError;
import aya.obj.Obj;
import aya.obj.block.Block;
import aya.parser.SourceStringRef;

public class TupleInstruction extends Instruction {	
	Block[] elements;
	
	/** 
	 * Tuples will not need to be resized during runtime
	 * @param blocks
	 */
	public TupleInstruction(SourceStringRef source, Block[] blocks) {
		super(source);
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
				EmptyStackError e2 = new EmptyStackError("Empty stack during evaluation of tuple");
				e2.addContext(this, elements[i]);
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
	public ReprStream repr(ReprStream stream) {
		stream.print("(");
		for(int i = 0; i < elements.length; i++) {
			if(i!=0) {
				stream.print(", ");
			}
			elements[i].repr(stream, false);
		}
		stream.print(")");
		return stream;
	}
	
}
