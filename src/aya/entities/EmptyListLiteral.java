package aya.entities;

import java.util.ArrayList;
import java.util.Stack;

import aya.obj.Obj;
import aya.obj.list.GenericList;
import aya.obj.list.List;

/** Specialization of a list literal which always returns an empty list */
public class EmptyListLiteral extends ListLiteral {
	
	public static final EmptyListLiteral INSTANCE = new EmptyListLiteral();
	
	protected EmptyListLiteral() {
		super(new InstructionStack(), 0);
	}

	@Override
	public int getPops() {
		return 0;
	}
	
	@Override
	public List getListCopy(Stack<Obj> outerStack) {
		return new GenericList(new ArrayList<Obj>());
	}
	
	@Override
	public List toList() {
		return new GenericList(new ArrayList<Obj>());
	}
	
	@Override
	public EmptyListLiteral duplicate() {
		// State is never modified, okay to return self
		return this;
	}
	
	@Override
	public String toString() {
		return "[]";
	}

	public Obj getListCopy() {
		return new GenericList(new ArrayList<Obj>());
	}
}
