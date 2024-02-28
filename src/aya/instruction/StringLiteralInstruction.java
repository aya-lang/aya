package aya.instruction;

import aya.ReprStream;
import aya.obj.block.BlockEvaluator;
import aya.obj.list.List;
import aya.parser.SourceStringRef;

public class StringLiteralInstruction extends Instruction {

	final String _str;
	
	public StringLiteralInstruction(SourceStringRef source, String str) {
		super(source);
		_str = str;
	}

	@Override
	public void execute(BlockEvaluator blockEvaluator) {
		blockEvaluator.push(List.fromString(_str));
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print("\"" + _str + "\"");
		return stream;
	}

}
