package aya.instruction;

import java.util.LinkedList;

import aya.obj.Obj;
import aya.obj.block.Block;

public class DataInstruction extends Instruction {
	
	Obj _data;
	
	public static boolean isa(Instruction i, byte type) {
		return i instanceof DataInstruction && ((DataInstruction)i).objIsa(type);
	}
	
	/** Assumes that this instruction has already been checked with isBlock */
	public static Block getBlock(Instruction i) {
		return  (Block)( ((DataInstruction)i).getData() );
	}

	/** If i is a DataInstruction with a single block, add the block instructions,
	 * otherwise add the instruction
	 */
	public static void addOrMergeInstruction(Block b, Instruction i) {
		if (i instanceof DataInstruction && ((DataInstruction)i).objIsa(Obj.BLOCK)) {
			b.addAll(DataInstruction.getBlock(i).getInstructions().getInstrucionList());
		} else {
			b.add(i);
		}
	}
	
	public DataInstruction(Obj data) {
		_data = data;
	}
	
	public boolean objIsa(byte id) {
		return _data.isa(id);
	}
	
	public Obj getData() {
		return _data;
	}

	@Override
	public void execute(Block b) {
		b.push(_data);

	}

	@Override
	protected String repr(LinkedList<Long> visited) {
		return _data.repr();
	}

}
