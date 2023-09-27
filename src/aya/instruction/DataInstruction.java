package aya.instruction;

import aya.ReprStream;
import aya.obj.Obj;
import aya.obj.block.Block;

public class DataInstruction extends Instruction {
	
	Obj _data;
	
	public static boolean isa(Instruction i, byte type) {
		return i instanceof DataInstruction && ((DataInstruction)i).objIsa(type);
	}
	
	/** If i is a DataInstruction with a single block, add the block instructions,
	 * otherwise add the instruction
	 */
	/*public static void addOrMergeInstruction(Block b, Instruction i) {
		if (i instanceof DataInstruction && ((DataInstruction)i).objIsa(Obj.BLOCK)) {
			b.addAll(DataInstruction.getBlock(i).getInstructions().getInstrucionList());
		} else {
			b.add(i);
		}
	}*/
	
	public DataInstruction(Obj data) {
		super(null); // Don't need to keep track of source for data instructions
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
	public ReprStream repr(ReprStream stream) {
		_data.repr(stream);
		return stream;
	}

}
