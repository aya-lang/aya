package aya.instruction;

import aya.ReprStream;
import aya.eval.BlockEvaluator;
import aya.obj.Obj;

public class DataInstruction extends Instruction {
	
	Obj _data;
	
	public static boolean isa(Instruction i, byte type) {
		return i instanceof DataInstruction && ((DataInstruction)i).objIsa(type);
	}
	
	/** If i is a DataInstruction with a single blockEvaluator, add the blockEvaluator instructions,
	 * otherwise add the instruction
	 */
	/*public static void addOrMergeInstruction(BlockEvaluator b, Instruction i) {
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
	public void execute(BlockEvaluator b) {
		b.push(_data);

	}

	@Override
	public ReprStream repr(ReprStream stream) {
		_data.repr(stream);
		return stream;
	}

}
