package aya.instruction;

import java.util.Stack;

import aya.eval.ExecutionContext;
import aya.obj.Obj;
import aya.obj.list.List;

/** Specialization of a list literal which always returns an empty list */
public class EmptyListLiteralInstruction extends ListLiteralInstruction {
	
	public static final EmptyListLiteralInstruction INSTANCE = new EmptyListLiteralInstruction();
	
	protected EmptyListLiteralInstruction() {
		super(null, new InstructionStack(), 0);
	}

	@Override
	public int getPops() {
		return 0;
	}
	
	@Override
	public List getListCopy(ExecutionContext context, Stack<Obj> outerStack) {
		return new List();
	}
	
	@Override
	public List toListNoEval() {
		return new List();
	}
	
//	@Override
//	public EmptyListLiteralInstruction duplicate() {
//		// State is never modified, okay to return self
//		return this;
//	}
	
	@Override
	public String toString() {
		return "[]";
	}

	public Obj getListCopy() {
		return new List();
	}
}
