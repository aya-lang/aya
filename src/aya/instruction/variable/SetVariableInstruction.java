package aya.instruction.variable;

import aya.ReprStream;
import aya.eval.BlockEvaluator;
import aya.obj.symbol.Symbol;
import aya.parser.SourceStringRef;

public class SetVariableInstruction extends VariableInstruction {
	
	public SetVariableInstruction(SourceStringRef source, Symbol var) {
		super(source, var);
	}
	
	@Override
	public void execute(BlockEvaluator b) {
		b.getContext().getVars().setVar(variable_, b.peek());
	}
	
	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print(":" + variable_.name());
		return stream;
	}
}
