package aya.instruction.variable;

import aya.Aya;
import aya.ReprStream;
import aya.obj.block.Block;
import aya.obj.symbol.Symbol;
import aya.parser.SourceStringRef;

public class SetVariableInstruction extends VariableInstruction {
	
	public SetVariableInstruction(SourceStringRef source, Symbol var) {
		super(source, var);
	}
	
	@Override
	public void execute(Block b) {
		Aya.getInstance().getVars().setVar(variable_, b.peek());
	}
	
	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print(":" + variable_.name());
		return stream;
	}
}
