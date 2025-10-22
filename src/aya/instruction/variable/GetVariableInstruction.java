package aya.instruction.variable;

import aya.ReprStream;
import aya.eval.BlockEvaluator;
import aya.instruction.flag.PopCallstackInstruction;
import aya.obj.Obj;
import aya.obj.block.StaticBlock;
import aya.obj.symbol.Symbol;
import aya.parser.SourceStringRef;
import aya.util.Casting;

public class GetVariableInstruction extends VariableInstruction {

	public GetVariableInstruction(SourceStringRef source, Symbol var) {
		super(source, var);
	}
	
	@Override
	public void execute(BlockEvaluator b) {
		for (Symbol variable : variables_) {
			Obj o = b.getContext().getVars().getVar(variable);
			this.addOrDumpVar(o, b);
		}
	}
	
	/**
	 * If o is a blockEvaluator, dump it's instructions. Otherwise, add it to the stack
	 * @param o
	 * @param b
	 */
	public void addOrDumpVar(Obj o, BlockEvaluator b) {
		if (o.isa(Obj.BLOCK)) {
			dumpBlock(Casting.asStaticBlock(o), b);
		} else {
			b.push(o);
		}
	}

	public void dumpBlock(StaticBlock block_to_dump, BlockEvaluator evaluator) {
		evaluator.getContext().getCallStack().push(this,
				block_to_dump.getReturnTypeCheck(),
				block_to_dump.getNumArgs(),
				evaluator.getStack().size());
		evaluator.add(PopCallstackInstruction.INSTANCE);
		//b.getInstructions().addAll(block_to_dump.getInstructions().getInstrucionList());
		evaluator.dump(block_to_dump);
	}

	@Override
	public ReprStream repr(ReprStream stream) {
		stream.print(varName_);
		return stream;
	}
}
