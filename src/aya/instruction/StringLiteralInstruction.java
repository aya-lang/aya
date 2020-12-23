package aya.instruction;

import aya.ReprStream;
import aya.obj.block.Block;
import aya.obj.list.List;

public class StringLiteralInstruction extends Instruction {

	final String _str;
	
	public StringLiteralInstruction(String str) {
		_str = str;
	}

	@Override
	public void execute(Block block) {
		block.push(List.fromString(_str));
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print("\"" + _str + "\"");
		return stream;
	}

}
