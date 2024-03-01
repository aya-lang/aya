package aya.instruction;

import java.util.ArrayList;
import java.util.EmptyStackException;

import aya.ReprStream;
import aya.eval.BlockEvaluator;
import aya.exceptions.runtime.EmptyStackError;
import aya.obj.Obj;
import aya.obj.block.BlockUtils;
import aya.obj.block.StaticBlock;
import aya.parser.SourceStringRef;

public class TupleInstruction extends Instruction {	
	StaticBlock[] elements;
	
	/** 
	 * Tuples will not need to be resized during runtime
	 * @param blocks
	 */
	public TupleInstruction(SourceStringRef source, StaticBlock[] blocks) {
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
			BlockEvaluator evaluator = new BlockEvaluator();
			evaluator.dump(elements[i]);
			try {
				evaluator.eval();
			} catch (EmptyStackException e) {
				EmptyStackError e2 = new EmptyStackError("Empty stack during evaluation of tuple");
				e2.setSource(this.getSource());
				throw e2;
			}
			out.add(evaluator.pop());
		}
		return out;
	}
	
	@Override
	public void execute(BlockEvaluator b) {
		b.getStack().addAll(evalToResults());
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print("(");
		for(int i = 0; i < elements.length; i++) {
			if(i!=0) {
				stream.print(", ");
			}
			BlockUtils.repr(stream, elements[i], false);
		}
		stream.print(")");
		return stream;
	}
	
}
