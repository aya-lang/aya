package aya.instruction;

import java.util.LinkedList;

import aya.obj.block.Block;

public abstract class Instruction {

	public abstract void execute(Block block);
	
	public final String repr() {
		LinkedList<Long> visited = new LinkedList<Long>();
		return repr(visited);
	}
	
	protected abstract String repr(LinkedList<Long> visited);
	
	@Override
	public String toString() {
		return repr();
	}

}
