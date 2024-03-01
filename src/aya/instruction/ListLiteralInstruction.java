package aya.instruction;

import java.util.ArrayList;
import java.util.Stack;

import aya.ReprStream;
import aya.eval.AyaThread;
import aya.eval.BlockEvaluator;
import aya.obj.Obj;
import aya.obj.block.BlockUtils;
import aya.obj.block.StaticBlock;
import aya.obj.list.List;
import aya.parser.SourceStringRef;

public class ListLiteralInstruction extends Instruction {
	//The number of items that should be popped from the stack and added to the front of this list
	private int _num_captures;
	private InstructionStack _is;
	private StaticBlock _block; // stored so we only need to run fromIS once
	
	public ListLiteralInstruction(SourceStringRef source, InstructionStack is, int num_pops) {
		super(source);
		_num_captures = num_pops;
		_is = is;
		_block = BlockUtils.fromIS(is);
	}
	
	public InstructionStack getInstructions() {
		return _is;
	}
	
	public int getPops() {
		return _num_captures;
	}
	
	public List getListCopy(AyaThread context, Stack<Obj> outerStack) {
		BlockEvaluator b = context.createEvaluator();
		b.dump(_block);
		int p = _num_captures;
		
		//Remove the pops from the outer stack
		while (p > 0) {
			p--;
			b.getInstructions().push(outerStack.pop());
		}
		
		b.eval();
		return new List(new ArrayList<Obj>(b.getStack()));
	}
	
	/** Returns null if is not list literal */
	public List toListNoEval() {
		ArrayList<Obj> items = new ArrayList<Obj>();
		ArrayList<Instruction> instrs = _is.getInstrucionList();
		for (int i = instrs.size()-1; i>=0; i--) {
			if (instrs.get(i) instanceof DataInstruction) {
				items.add(((DataInstruction)(instrs.get(i))).getData());
			} else {
				return null;
			}
		}
		return new List(items);
	}
	
	//public ListLiteralInstruction duplicate() {
	//	ListLiteralInstruction ll = new ListLiteralInstruction(getSource(), blockEvaluator.getInstructions().duplicate(), num_captures);
	//	ll.blockEvaluator.getStack().addAll(this.blockEvaluator.getStack());
	//	return ll;
	//}
	

	@Override
	public void execute(BlockEvaluator b) {
		b.push(getListCopy(b.getContext(), b.getStack()));
		
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print("[");
		if (_num_captures > 0) {
			stream.print(_num_captures + "| ");
		}
		_is.repr(stream);
		stream.print("]");
		return stream;
	}
}

