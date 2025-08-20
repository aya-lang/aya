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
		int numVars = variables_.length;
		for (int i = 0; i < numVars; i++) {
			b.getContext().getVars().setVar(variables_[i], b.peek(numVars - (i + 1)));
		}
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print(":" + varName_);
		return stream;
	}
}
